package com.novelgraph.service;

import com.novelgraph.dto.RelTypeConfigDTO;
import com.novelgraph.dto.RelTypeConfigVO;

import java.util.List;

/**
 * 关系类型配置服务接口
 *
 * @author novelgraph
 */
public interface RelTypeConfigService {

    /**
     * 查询某小说的关系类型列表
     */
    List<RelTypeConfigVO> listByNovel(String novelId);

    /**
     * 自定义关系类型
     */
    RelTypeConfigVO create(String novelId, RelTypeConfigDTO dto);

    /**
     * 为小说复制系统预置的 25 种关系类型（新建小说时调用）
     */
    void copyPresetTypesToNovel(String novelId);
}
