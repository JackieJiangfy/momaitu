package com.novelgraph.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novelgraph.common.BusinessException;
import com.novelgraph.common.ResultCode;
import com.novelgraph.dto.RelationshipQueryDTO;
import com.novelgraph.dto.RelationshipSaveDTO;
import com.novelgraph.dto.RelationshipVO;
import com.novelgraph.entity.NovelCharacter;
import com.novelgraph.entity.NovelRelationship;
import com.novelgraph.mapper.NovelCharacterMapper;
import com.novelgraph.mapper.NovelRelationshipMapper;
import com.novelgraph.service.NovelService;
import com.novelgraph.service.RelationshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关系服务实现
 * - 校验：source/target 必须属于该 novel
 * - 唯一性：(novelId, sourceId, targetId, relType) 组合唯一
 * - 列表返回时冗余 sourceName/targetName 便于前端展示
 *
 * @author novelgraph
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RelationshipServiceImpl implements RelationshipService {

    private final NovelRelationshipMapper relationshipMapper;
    private final NovelCharacterMapper characterMapper;
    private final NovelService novelService;

    @Override
    public IPage<RelationshipVO> page(String novelId, RelationshipQueryDTO query) {
        novelService.checkOwnership(novelId);

        Page<NovelRelationship> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<NovelRelationship> wrapper = new LambdaQueryWrapper<NovelRelationship>()
                .eq(NovelRelationship::getNovelId, novelId)
                .orderByDesc(NovelRelationship::getCreatedAt);

        if (StrUtil.isNotBlank(query.getSourceId())) {
            wrapper.eq(NovelRelationship::getSourceId, query.getSourceId());
        }
        if (StrUtil.isNotBlank(query.getTargetId())) {
            wrapper.eq(NovelRelationship::getTargetId, query.getTargetId());
        }
        if (StrUtil.isNotBlank(query.getRelType())) {
            wrapper.eq(NovelRelationship::getRelType, query.getRelType());
        }
        if (StrUtil.isNotBlank(query.getCategory())) {
            wrapper.eq(NovelRelationship::getCategory, query.getCategory());
        }

        IPage<NovelRelationship> relPage = relationshipMapper.selectPage(page, wrapper);

        // 批量查询角色名（避免 N+1：先收集所有 sourceId/targetId，再一次性查询）
        Map<String, String> nameCache = new HashMap<>();
        for (NovelRelationship rel : relPage.getRecords()) {
            nameCache.putIfAbsent(rel.getSourceId(), "");
            nameCache.putIfAbsent(rel.getTargetId(), "");
        }
        if (!nameCache.isEmpty()) {
            for (String cid : nameCache.keySet()) {
                NovelCharacter c = characterMapper.selectById(cid);
                if (c != null) {
                    nameCache.put(cid, c.getName());
                }
            }
        }
        final Map<String, String> finalCache = nameCache;

        return relPage.convert(rel -> {
            RelationshipVO vo = new RelationshipVO();
            BeanUtil.copyProperties(rel, vo);
            vo.setSourceName(finalCache.getOrDefault(rel.getSourceId(), ""));
            vo.setTargetName(finalCache.getOrDefault(rel.getTargetId(), ""));
            return vo;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RelationshipVO create(String novelId, RelationshipSaveDTO dto) {
        novelService.checkOwnership(novelId);

        // 校验 sourceId != targetId
        if (dto.getSourceId().equals(dto.getTargetId())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "源角色和目标角色不能相同");
        }

        // 校验 source 和 target 属于该 novel
        checkCharacterInNovel(novelId, dto.getSourceId(), "源角色");
        checkCharacterInNovel(novelId, dto.getTargetId(), "目标角色");

        // 唯一性校验
        Long exists = relationshipMapper.selectCount(
                new LambdaQueryWrapper<NovelRelationship>()
                        .eq(NovelRelationship::getNovelId, novelId)
                        .eq(NovelRelationship::getSourceId, dto.getSourceId())
                        .eq(NovelRelationship::getTargetId, dto.getTargetId())
                        .eq(NovelRelationship::getRelType, dto.getRelType()));
        if (exists != null && exists > 0) {
            throw new BusinessException(ResultCode.RELATIONSHIP_DUPLICATE);
        }

        NovelRelationship rel = new NovelRelationship();
        BeanUtil.copyProperties(dto, rel);
        rel.setNovelId(novelId);
        if (rel.getDirected() == null) {
            rel.setDirected(0);
        }
        if (rel.getIntensity() == null) {
            rel.setIntensity(5);
        }
        relationshipMapper.insert(rel);
        log.info("关系创建成功: id={}, {}-{}-{}", rel.getId(), dto.getSourceId(), dto.getRelType(), dto.getTargetId());

        return toVO(rel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RelationshipVO update(String novelId, String relationshipId, RelationshipSaveDTO dto) {
        NovelRelationship rel = checkRelationshipInNovel(novelId, relationshipId);

        // 如果 source/target 变更，重新校验
        if (!rel.getSourceId().equals(dto.getSourceId()) || !rel.getTargetId().equals(dto.getTargetId())) {
            if (dto.getSourceId().equals(dto.getTargetId())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "源角色和目标角色不能相同");
            }
            checkCharacterInNovel(novelId, dto.getSourceId(), "源角色");
            checkCharacterInNovel(novelId, dto.getTargetId(), "目标角色");
        }

        // 唯一性校验（排除自身）
        Long exists = relationshipMapper.selectCount(
                new LambdaQueryWrapper<NovelRelationship>()
                        .eq(NovelRelationship::getNovelId, novelId)
                        .eq(NovelRelationship::getSourceId, dto.getSourceId())
                        .eq(NovelRelationship::getTargetId, dto.getTargetId())
                        .eq(NovelRelationship::getRelType, dto.getRelType())
                        .ne(NovelRelationship::getId, relationshipId));
        if (exists != null && exists > 0) {
            throw new BusinessException(ResultCode.RELATIONSHIP_DUPLICATE);
        }

        BeanUtil.copyProperties(dto, rel);
        relationshipMapper.updateById(rel);
        log.info("关系更新成功: id={}", relationshipId);
        return toVO(rel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String novelId, String relationshipId) {
        NovelRelationship rel = checkRelationshipInNovel(novelId, relationshipId);
        relationshipMapper.deleteById(rel.getId());
        log.info("关系删除成功: id={}", relationshipId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchDelete(String novelId, List<String> relationshipIds) {
        if (relationshipIds == null || relationshipIds.isEmpty()) {
            return 0;
        }
        novelService.checkOwnership(novelId);
        int count = relationshipMapper.deleteBatchIds(relationshipIds);
        log.info("批量删除关系成功: novelId={}, count={}", novelId, count);
        return count;
    }

    @Override
    public NovelRelationship checkRelationshipInNovel(String novelId, String relationshipId) {
        novelService.checkOwnership(novelId);
        NovelRelationship rel = relationshipMapper.selectById(relationshipId);
        if (rel == null) {
            throw new BusinessException(ResultCode.RELATIONSHIP_NOT_FOUND);
        }
        if (!novelId.equals(rel.getNovelId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "关系不属于该小说");
        }
        return rel;
    }

    /**
     * 校验角色属于指定小说
     */
    private void checkCharacterInNovel(String novelId, String characterId, String label) {
        NovelCharacter c = characterMapper.selectById(characterId);
        if (c == null) {
            throw new BusinessException(ResultCode.CHARACTER_NOT_FOUND, label + "不存在");
        }
        if (!novelId.equals(c.getNovelId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, label + "不属于该小说");
        }
    }

    private RelationshipVO toVO(NovelRelationship rel) {
        RelationshipVO vo = new RelationshipVO();
        BeanUtil.copyProperties(rel, vo);
        // 填充角色名
        NovelCharacter source = characterMapper.selectById(rel.getSourceId());
        if (source != null) {
            vo.setSourceName(source.getName());
        }
        NovelCharacter target = characterMapper.selectById(rel.getTargetId());
        if (target != null) {
            vo.setTargetName(target.getName());
        }
        return vo;
    }
}
