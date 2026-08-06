package com.novelgraph.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.novelgraph.dto.RelationshipQueryDTO;
import com.novelgraph.dto.RelationshipSaveDTO;
import com.novelgraph.dto.RelationshipVO;
import com.novelgraph.dto.moliu.MoliuRelationshipSyncDTO;
import com.novelgraph.entity.NovelRelationship;

import java.util.List;
import java.util.Map;

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
     * 批量删除关系（校验小说归属）
     *
     * @return 成功删除数量
     */
    Integer batchDelete(String novelId, List<String> relationshipIds);

    /**
     * 批量 upsert 关系（来自墨流 LLM 抽取）
     * - 按角色名查找 ID，找不到则跳过
     * - 按 (novelId, sourceId, targetId, relType) upsert
     *
     * @return {"success": n, "skipped": m, "updated": k}
     */
    Map<String, Object> batchSync(String novelId, List<MoliuRelationshipSyncDTO> list);

    /**
     * 校验关系属于指定小说（内部使用）
     */
    NovelRelationship checkRelationshipInNovel(String novelId, String relationshipId);
}
