package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 图谱边 VO（对应前端 RelationshipEdge）
 * 字段命名与设计文档 5.2 节一致，供前端 G6 直接消费
 *
 * @author novelgraph
 */
@Data
public class GraphEdgeVO implements Serializable {

    private String id;
    private String source;
    private String target;
    private String relType;
    private String category;
    private Boolean directed;
    private Integer intensity;
}
