package com.novelgraph.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novelgraph.common.BusinessException;
import com.novelgraph.common.ResultCode;
import com.novelgraph.dto.RelTypeConfigDTO;
import com.novelgraph.dto.RelTypeConfigVO;
import com.novelgraph.entity.NovelRelTypeConfig;
import com.novelgraph.mapper.NovelRelTypeConfigMapper;
import com.novelgraph.service.NovelService;
import com.novelgraph.service.RelTypeConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 关系类型配置服务实现
 * - 系统预置模板：novel_id = 'SYSTEM_PRESET'（25 种关系类型）
 * - 新建小说时从模板复制一份
 * - 用户可自定义新关系类型
 *
 * @author novelgraph
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RelTypeConfigServiceImpl implements RelTypeConfigService {

    private final NovelRelTypeConfigMapper relTypeConfigMapper;

    @Autowired
    @Lazy
    private NovelService novelService;

    @Override
    public List<RelTypeConfigVO> listByNovel(String novelId) {
        novelService.checkOwnership(novelId);
        List<NovelRelTypeConfig> list = relTypeConfigMapper.selectList(
                new LambdaQueryWrapper<NovelRelTypeConfig>()
                        .eq(NovelRelTypeConfig::getNovelId, novelId)
                        .orderByAsc(NovelRelTypeConfig::getSortOrder));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RelTypeConfigVO create(String novelId, RelTypeConfigDTO dto) {
        novelService.checkOwnership(novelId);

        // 同名校验
        Long exists = relTypeConfigMapper.selectCount(
                new LambdaQueryWrapper<NovelRelTypeConfig>()
                        .eq(NovelRelTypeConfig::getNovelId, novelId)
                        .eq(NovelRelTypeConfig::getTypeName, dto.getTypeName()));
        if (exists != null && exists > 0) {
            throw new BusinessException(ResultCode.CONFLICT, "该小说下已存在同名关系类型");
        }

        NovelRelTypeConfig config = new NovelRelTypeConfig();
        BeanUtil.copyProperties(dto, config);
        config.setNovelId(novelId);
        if (config.getSortOrder() == null) {
            config.setSortOrder(0);
        }
        relTypeConfigMapper.insert(config);
        log.info("关系类型创建成功: novelId={}, typeName={}", novelId, dto.getTypeName());
        return toVO(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyPresetTypesToNovel(String novelId) {
        // 查询系统预置的 25 种关系类型
        List<NovelRelTypeConfig> presets = relTypeConfigMapper.selectList(
                new LambdaQueryWrapper<NovelRelTypeConfig>()
                        .eq(NovelRelTypeConfig::getNovelId, "SYSTEM_PRESET")
                        .orderByAsc(NovelRelTypeConfig::getSortOrder));

        for (NovelRelTypeConfig preset : presets) {
            NovelRelTypeConfig copy = new NovelRelTypeConfig();
            copy.setNovelId(novelId);
            copy.setTypeName(preset.getTypeName());
            copy.setCategory(preset.getCategory());
            copy.setIcon(preset.getIcon());
            copy.setSortOrder(preset.getSortOrder());
            relTypeConfigMapper.insert(copy);
        }
        log.info("预置关系类型复制完成: novelId={}, 数量={}", novelId, presets.size());
    }

    private RelTypeConfigVO toVO(NovelRelTypeConfig config) {
        RelTypeConfigVO vo = new RelTypeConfigVO();
        BeanUtil.copyProperties(config, vo);
        return vo;
    }
}
