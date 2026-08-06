package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 小说响应 VO（列表和详情共用）
 * 列表场景下 characterCount/relationCount 由查询统计填充；
 * 详情场景下同上。
 *
 * @author novelgraph
 */
@Data
public class NovelVO implements Serializable {

    private String id;
    private String userId;
    private String title;
    private String author;
    private String description;
    private String coverUrl;
    private String status;
    private Integer characterCount;
    private Integer relationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
