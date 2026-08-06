package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关系类型配置响应 VO
 *
 * @author novelgraph
 */
@Data
public class RelTypeConfigVO implements Serializable {

    private String id;
    private String novelId;
    private String typeName;
    private String category;
    private String icon;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}
