package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 图谱节点 VO（对应前端 CharacterNode）
 * 字段命名与设计文档 5.2 节一致，供前端 G6 直接消费
 *
 * @author novelgraph
 */
@Data
public class GraphNodeVO implements Serializable {

    private String id;
    private String name;
    private String alias;
    private String faction;
    private String roleType;
    private String species;
    private String avatarUrl;
    /** 关联关系数（用于计算节点大小） */
    private Integer relationCount;
}
