package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色列表查询 DTO
 *
 * @author novelgraph
 */
@Data
public class CharacterQueryDTO implements Serializable {

    /** 搜索关键词（匹配名称或别名） */
    private String keyword;

    /** 势力过滤 */
    private String faction;

    /** 定位过滤：主角/配角/反派/路人 */
    private String roleType;

    /** 种族过滤 */
    private String species;

    /** 当前页码，默认 1 */
    private Integer page = 1;

    /** 每页条数，默认 20 */
    private Integer size = 20;
}
