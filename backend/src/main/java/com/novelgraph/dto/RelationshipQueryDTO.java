package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 关系列表查询 DTO
 *
 * @author novelgraph
 */
@Data
public class RelationshipQueryDTO implements Serializable {

    /** 源角色ID过滤 */
    private String sourceId;

    /** 目标角色ID过滤 */
    private String targetId;

    /** 关系类型过滤 */
    private String relType;

    /** 关系性质过滤：positive/neutral/negative */
    private String category;

    /** 当前页码，默认 1 */
    private Integer page = 1;

    /** 每页条数，默认 50（关系数据通常较多） */
    private Integer size = 50;
}
