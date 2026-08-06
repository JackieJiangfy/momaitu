package com.novelgraph.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.novelgraph.dto.RelationshipQueryDTO;
import com.novelgraph.dto.RelationshipSaveDTO;
import com.novelgraph.dto.RelationshipVO;
import com.novelgraph.entity.NovelRelationship;

/**
 * 关系服务接口
 *
 * @author novelgraph
 */
public interface RelationshipService {

    /**
     * 分页查询关系列表（带角色名冗余）
     */
    IPage<RelationshipVO> page(String novelId, RelationshipQueryDTO query);

    /**
     * 添加关系（校验：source/target 属于该 novel，唯一性约束）
     */
    RelationshipVO create(String novelId, RelationshipSaveDTO dto);

    /**
     * 编辑关系
     */
    RelationshipVO update(String novelId, String relationshipId, RelationshipSaveDTO dto);

    /**
     * 删除关系
     */
    void delete(String novelId, String relationshipId);

    /**
     * 校验关系属于指定小说（内部使用）
     */
    NovelRelationship checkRelationshipInNovel(String novelId, String relationshipId);
}
