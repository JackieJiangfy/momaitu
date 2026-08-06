package com.novelgraph.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 自定义关系类型请求 DTO
 *
 * @author novelgraph
 */
@Data
public class RelTypeConfigDTO implements Serializable {

    @NotBlank(message = "关系类型名称不能为空")
    @Size(max = 32, message = "关系类型名称长度不能超过 32 个字符")
    private String typeName;

    @NotBlank(message = "关系性质不能为空")
    private String category;

    @Size(max = 32, message = "图标标识长度不能超过 32 个字符")
    private String icon;

    /** 排序权重，为空默认 0 */
    private Integer sortOrder;
}
