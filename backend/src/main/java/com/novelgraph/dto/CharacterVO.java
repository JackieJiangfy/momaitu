package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色响应 VO
 *
 * @author novelgraph
 */
@Data
public class CharacterVO implements Serializable {

    private String id;
    private String novelId;
    private String name;
    private String alias;
    private String faction;
    private String roleType;
    private String species;
    private String avatarUrl;
    private String description;
    private String firstChapter;
    private String powerLevel;
    private Integer sortOrder;
    /** 关联关系数（用于图谱节点大小计算） */
    private Integer relationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
