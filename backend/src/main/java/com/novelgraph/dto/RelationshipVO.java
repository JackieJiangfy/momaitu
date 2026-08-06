package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关系响应 VO（带角色名冗余，方便前端展示）
 *
 * @author novelgraph
 */
@Data
public class RelationshipVO implements Serializable {

    private String id;
    private String novelId;
    private String sourceId;
    private String targetId;
    /** 源角色名（冗余字段） */
    private String sourceName;
    /** 目标角色名（冗余字段） */
    private String targetName;
    private String relType;
    private String category;
    private Integer directed;
    private Integer intensity;
    private String description;
    private String startChapter;
    private String endChapter;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
