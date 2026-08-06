package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 小说列表查询 DTO
 *
 * @author novelgraph
 */
@Data
public class NovelQueryDTO implements Serializable {

    /** 搜索关键词（匹配标题或作者） */
    private String keyword;

    /** 状态过滤：ACTIVE/ARCHIVED，为空则查全部 */
    private String status;

    /** 当前页码，默认 1 */
    private Integer page = 1;

    /** 每页条数，默认 10 */
    private Integer size = 10;
}
