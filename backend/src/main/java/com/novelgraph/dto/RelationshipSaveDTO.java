package com.novelgraph.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 关系创建/编辑请求 DTO
 *
 * @author novelgraph
 */
@Data
public class RelationshipSaveDTO implements Serializable {

    @NotBlank(message = "源角色ID不能为空")
    private String sourceId;

    @NotBlank(message = "目标角色ID不能为空")
    private String targetId;

    @NotBlank(message = "关系类型不能为空")
    @Size(max = 32, message = "关系类型长度不能超过 32 个字符")
    private String relType;

    @NotBlank(message = "关系性质不能为空")
    private String category;

    /** 是否单向：0双向 1单向（如暗恋）。为空默认 0 */
    private Integer directed;

    /** 关系强度：1-10，为空默认 5 */
    private Integer intensity;

    @Size(max = 256, message = "关系说明长度不能超过 256 个字符")
    private String description;

    @Size(max = 32, message = "起始章节长度不能超过 32 个字符")
    private String startChapter;

    @Size(max = 32, message = "结束章节长度不能超过 32 个字符")
    private String endChapter;
}
