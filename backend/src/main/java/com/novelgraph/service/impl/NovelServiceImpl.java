package com.novelgraph.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novelgraph.common.BusinessException;
import com.novelgraph.common.ResultCode;
import com.novelgraph.dto.NovelQueryDTO;
import com.novelgraph.dto.NovelSaveDTO;
import com.novelgraph.dto.NovelVO;
import com.novelgraph.entity.Novel;
import com.novelgraph.mapper.NovelCharacterMapper;
import com.novelgraph.mapper.NovelMapper;
import com.novelgraph.mapper.NovelRelationshipMapper;
import com.novelgraph.service.NovelService;
import com.novelgraph.service.RelTypeConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 小说服务实现
 * - 所有操作基于当前登录用户的 userId
 * - 删除采用归档方式（status 置为 ARCHIVED），不真正逻辑删除
 * - 列表/详情返回时附带 characterCount 和 relationCount
 *
 * @author novelgraph
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NovelServiceImpl implements NovelService {

    private final NovelMapper novelMapper;
    private final NovelCharacterMapper characterMapper;
    private final NovelRelationshipMapper relationshipMapper;
    private final RelTypeConfigService relTypeConfigService;

    @Override
    public IPage<NovelVO> page(NovelQueryDTO query) {
        String userId = StpUtil.getLoginIdAsString();
        Page<Novel> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<Novel> wrapper = new LambdaQueryWrapper<Novel>()
                .eq(Novel::getUserId, userId)
                .orderByDesc(Novel::getUpdatedAt);

        // 状态过滤：默认查 ACTIVE，指定时按指定值
        if (StrUtil.isNotBlank(query.getStatus())) {
            wrapper.eq(Novel::getStatus, query.getStatus());
        } else {
            wrapper.eq(Novel::getStatus, "ACTIVE");
        }

        // 关键词搜索（匹配标题或作者）
        if (StrUtil.isNotBlank(query.getKeyword())) {
            String kw = query.getKeyword().trim();
            wrapper.and(w -> w.like(Novel::getTitle, kw).or().like(Novel::getAuthor, kw));
        }

        IPage<Novel> novelPage = novelMapper.selectPage(page, wrapper);
        return novelPage.convert(this::toVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NovelVO create(NovelSaveDTO dto) {
        String userId = StpUtil.getLoginIdAsString();
        Novel novel = new Novel();
        BeanUtil.copyProperties(dto, novel);
        novel.setUserId(userId);
        novel.setStatus("ACTIVE");
        novelMapper.insert(novel);
        // 复制 25 种系统预置关系类型到该小说
        relTypeConfigService.copyPresetTypesToNovel(novel.getId());
        log.info("小说创建成功: id={}, title={}, userId={}", novel.getId(), novel.getTitle(), userId);
        return toVO(novel);
    }

    @Override
    public NovelVO detail(String id) {
        Novel novel = checkOwnership(id);
        return toVO(novel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NovelVO update(String id, NovelSaveDTO dto) {
        Novel novel = checkOwnership(id);
        BeanUtil.copyProperties(dto, novel);
        novelMapper.updateById(novel);
        log.info("小说更新成功: id={}", id);
        return toVO(novel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        Novel novel = checkOwnership(id);
        // 归档而非真正删除，便于后续恢复
        novel.setStatus("ARCHIVED");
        novelMapper.updateById(novel);
        log.info("小说归档成功: id={}", id);
    }

    @Override
    public Novel checkOwnership(String novelId) {
        StpUtil.checkLogin();
        String userId = StpUtil.getLoginIdAsString();
        Novel novel = novelMapper.selectById(novelId);
        if (novel == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "小说不存在");
        }
        if (!userId.equals(novel.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权访问该小说");
        }
        return novel;
    }

    /**
     * 实体转 VO，并填充角色数和关系数
     */
    private NovelVO toVO(Novel novel) {
        NovelVO vo = new NovelVO();
        BeanUtil.copyProperties(novel, vo);
        // 统计角色数（未删除）
        Long charCount = characterMapper.selectCount(
                new LambdaQueryWrapper<com.novelgraph.entity.NovelCharacter>()
                        .eq(com.novelgraph.entity.NovelCharacter::getNovelId, novel.getId()));
        vo.setCharacterCount(charCount != null ? charCount.intValue() : 0);
        // 统计关系数（未删除）
        Long relCount = relationshipMapper.selectCount(
                new LambdaQueryWrapper<com.novelgraph.entity.NovelRelationship>()
                        .eq(com.novelgraph.entity.NovelRelationship::getNovelId, novel.getId()));
        vo.setRelationCount(relCount != null ? relCount.intValue() : 0);
        return vo;
    }
}
