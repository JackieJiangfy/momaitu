package com.novelgraph.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novelgraph.dto.moliu.MoliuChapterSyncDTO;
import com.novelgraph.dto.moliu.MoliuCharacterSyncDTO;
import com.novelgraph.dto.moliu.MoliuForeshadowSyncDTO;
import com.novelgraph.dto.moliu.MoliuNarratorSyncDTO;
import com.novelgraph.dto.moliu.MoliuWorldSyncDTO;
import com.novelgraph.entity.*;
import com.novelgraph.mapper.NovelCharacterMapper;
import com.novelgraph.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 墨流数据同步服务实现
 *
 * @author novelgraph
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MoliuSyncServiceImpl implements MoliuSyncService {

    private final NovelService novelService;
    private final NovelCharacterMapper characterMapper;
    private final NovelWorldSettingService worldSettingService;
    private final NovelNarratorService narratorService;
    private final NovelChapterService chapterService;
    private final NovelForeshadowService foreshadowService;

    // ============================ 角色 ============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncCharacter(String novelId, MoliuCharacterSyncDTO dto) {
        novelService.checkOwnership(novelId);

        NovelCharacter exist = characterMapper.selectOne(
                new LambdaQueryWrapper<NovelCharacter>()
                        .eq(NovelCharacter::getNovelId, novelId)
                        .eq(NovelCharacter::getName, dto.getName())
                        .last("LIMIT 1")
        );

        boolean isCreated = false;
        NovelCharacter c;
        if (exist != null) {
            c = exist;
        } else {
            c = new NovelCharacter();
            c.setNovelId(novelId);
            c.setName(dto.getName());
            c.setSortOrder(0);
            isCreated = true;
        }

        applyMoliuCharacterFields(c, dto);
        c.setMoliuSyncedAt(LocalDateTime.now());

        if (isCreated) {
            characterMapper.insert(c);
        } else {
            characterMapper.updateById(c);
        }

        log.info("墨流角色同步 {} novelId={} characterId={} name={}",
                isCreated ? "新建" : "更新", novelId, c.getId(), dto.getName());

        Map<String, Object> r = new HashMap<>();
        r.put("id", c.getId());
        r.put("isCreated", isCreated);
        return r;
    }

    /**
     * 将 DTO 的扁平与嵌套字段写入实体（不影响原 description/avatar 等基础字段）
     */
    @SuppressWarnings("unchecked")
    private void applyMoliuCharacterFields(NovelCharacter c, MoliuCharacterSyncDTO dto) {
        c.setOneLinePitch(dto.getOneLinePitch());
        c.setSpeechProfile(dto.getSpeechProfile());
        c.setSpeechSamples(dto.getSpeechSamples());
        c.setInnerVoiceStyle(dto.getInnerVoiceStyle());
        c.setBackstorySummary(dto.getBackstorySummary());
        c.setBackstoryImpact(dto.getBackstoryImpact());
        c.setHiddenClues(dto.getHiddenClues());

        // core 嵌套对象扁平化
        Map<String, Object> core = dto.getCore();
        if (core != null) {
            c.setCoreDesire((String) core.get("core_desire"));
            c.setSurfaceDesire((String) core.get("surface_desire"));
            c.setDeepFear((String) core.get("deep_fear"));
            Object vbl = core.get("value_bottom_line");
            if (vbl instanceof List) {
                c.setValueBottomLine(((List<Object>) vbl).stream()
                        .map(String::valueOf).collect(Collectors.toList()));
            }
        }

        // state 嵌套对象扁平化
        Map<String, Object> state = dto.getState();
        if (state != null) {
            c.setStatus((String) state.getOrDefault("status", "active"));
            c.setCurrentLocation((String) state.get("location"));
            c.setCurrentGoal((String) state.get("current_goal"));
            c.setCurrentEmotion((String) state.get("current_emotion"));
            c.setPhysicalState((String) state.get("physical_state"));
            Object res = state.get("resources");
            if (res instanceof List) {
                c.setResources(((List<Object>) res).stream()
                        .map(String::valueOf).collect(Collectors.toList()));
            }
            Object ki = state.get("known_info");
            if (ki instanceof List) {
                c.setKnownInfo(((List<Object>) ki).stream()
                        .map(String::valueOf).collect(Collectors.toList()));
            }
        }

        // appearance 直接存为 JSON
        c.setAppearance(dto.getAppearance());
    }

    // ============================ 世界观 ============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncWorld(String novelId, MoliuWorldSyncDTO dto) {
        novelService.checkOwnership(novelId);

        NovelWorldSetting exist = worldSettingService.getOne(
                new LambdaQueryWrapper<NovelWorldSetting>()
                        .eq(NovelWorldSetting::getNovelId, novelId)
                        .last("LIMIT 1")
        );

        boolean isCreated = false;
        NovelWorldSetting w;
        if (exist != null) {
            w = exist;
        } else {
            w = new NovelWorldSetting();
            w.setNovelId(novelId);
            isCreated = true;
        }

        BeanUtil.copyProperties(dto, w);
        w.setMoliuSyncedAt(LocalDateTime.now());

        if (isCreated) {
            worldSettingService.save(w);
        } else {
            worldSettingService.updateById(w);
        }

        log.info("墨流世界观同步 {} novelId={}", isCreated ? "新建" : "更新", novelId);

        Map<String, Object> r = new HashMap<>();
        r.put("id", w.getId());
        r.put("isCreated", isCreated);
        return r;
    }

    // ============================ 叙述者 ============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncNarrator(String novelId, MoliuNarratorSyncDTO dto) {
        novelService.checkOwnership(novelId);

        NovelNarrator exist = narratorService.getOne(
                new LambdaQueryWrapper<NovelNarrator>()
                        .eq(NovelNarrator::getNovelId, novelId)
                        .last("LIMIT 1")
        );

        boolean isCreated = false;
        NovelNarrator n;
        if (exist != null) {
            n = exist;
        } else {
            n = new NovelNarrator();
            n.setNovelId(novelId);
            isCreated = true;
        }

        BeanUtil.copyProperties(dto, n);
        n.setMoliuSyncedAt(LocalDateTime.now());

        if (isCreated) {
            narratorService.save(n);
        } else {
            narratorService.updateById(n);
        }

        log.info("墨流叙述者同步 {} novelId={}", isCreated ? "新建" : "更新", novelId);

        Map<String, Object> r = new HashMap<>();
        r.put("id", n.getId());
        r.put("isCreated", isCreated);
        return r;
    }

    // ============================ 章节 ============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncChapter(String novelId, MoliuChapterSyncDTO dto) {
        novelService.checkOwnership(novelId);

        NovelChapter exist = chapterService.getOne(
                new LambdaQueryWrapper<NovelChapter>()
                        .eq(NovelChapter::getNovelId, novelId)
                        .eq(NovelChapter::getChapterNum, dto.getChapterNum())
                        .last("LIMIT 1")
        );

        boolean isCreated = false;
        NovelChapter ch;
        if (exist != null) {
            ch = exist;
        } else {
            ch = new NovelChapter();
            ch.setNovelId(novelId);
            ch.setCreatedAt(LocalDateTime.now());
            isCreated = true;
        }

        BeanUtil.copyProperties(dto, ch);
        ch.setMoliuSyncedAt(LocalDateTime.now());
        ch.setUpdatedAt(LocalDateTime.now());

        if (isCreated) {
            chapterService.save(ch);
        } else {
            chapterService.updateById(ch);
        }

        log.info("墨流章节同步 {} novelId={} chapterNum={}",
                isCreated ? "新建" : "更新", novelId, dto.getChapterNum());

        Map<String, Object> r = new HashMap<>();
        r.put("id", ch.getId());
        r.put("isCreated", isCreated);
        return r;
    }

    // ============================ 伏笔 ============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncForeshadow(String novelId, MoliuForeshadowSyncDTO dto) {
        novelService.checkOwnership(novelId);

        NovelForeshadow exist = foreshadowService.getOne(
                new LambdaQueryWrapper<NovelForeshadow>()
                        .eq(NovelForeshadow::getNovelId, novelId)
                        .eq(NovelForeshadow::getMoliuId, dto.getMoliuId())
                        .last("LIMIT 1")
        );

        boolean isCreated = false;
        NovelForeshadow f;
        if (exist != null) {
            f = exist;
        } else {
            f = new NovelForeshadow();
            f.setNovelId(novelId);
            f.setMoliuId(dto.getMoliuId());
            f.setCreatedAt(LocalDateTime.now());
            isCreated = true;
        }

        BeanUtil.copyProperties(dto, f);
        f.setMoliuSyncedAt(LocalDateTime.now());

        // 关联角色名 → 角色ID 列表
        if (dto.getRelatedCharacters() != null && !dto.getRelatedCharacters().isEmpty()) {
            List<NovelCharacter> chars = characterMapper.selectList(
                    new LambdaQueryWrapper<NovelCharacter>()
                            .eq(NovelCharacter::getNovelId, novelId)
                            .in(NovelCharacter::getName, dto.getRelatedCharacters())
            );
            Map<String, String> nameToId = chars.stream()
                    .collect(Collectors.toMap(NovelCharacter::getName, NovelCharacter::getId, (a, b) -> a));
            List<String> charIds = dto.getRelatedCharacters().stream()
                    .map(nameToId::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            f.setRelatedCharacters(charIds);
        }

        if (isCreated) {
            foreshadowService.save(f);
        } else {
            foreshadowService.updateById(f);
        }

        log.info("墨流伏笔同步 {} novelId={} moliuId={}",
                isCreated ? "新建" : "更新", novelId, dto.getMoliuId());

        Map<String, Object> r = new HashMap<>();
        r.put("id", f.getId());
        r.put("isCreated", isCreated);
        return r;
    }
}
