package com.novelgraph.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 小说创建/编辑请求 DTO
 *
 * @author novelgraph
 */
@Data
public class NovelSaveDTO implements Serializable {

    @NotBlank(message = "小说名称不能为空")
    @Size(max = 128, message = "小说名称长度不能超过 128 个字符")
    private String title;

    @Size(max = 64, message = "作者长度不能超过 64 个字符")
    private String author;

    @Size(max = 2000, message = "简介长度不能超过 2000 个字符")
    private String description;

    @Size(max = 512, message = "封面URL长度不能超过 512 个字符")
    private String coverUrl;
}
