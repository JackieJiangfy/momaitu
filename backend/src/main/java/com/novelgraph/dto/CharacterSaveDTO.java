package com.novelgraph.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 角色创建/编辑请求 DTO
 *
 * @author novelgraph
 */
@Data
public class CharacterSaveDTO implements Serializable {

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 64, message = "角色名称长度不能超过 64 个字符")
    private String name;

    @Size(max = 256, message = "别名长度不能超过 256 个字符")
    private String alias;

    @Size(max = 64, message = "势力长度不能超过 64 个字符")
    private String faction;

    @Size(max = 32, message = "定位长度不能超过 32 个字符")
    private String roleType;

    @Size(max = 32, message = "种族长度不能超过 32 个字符")
    private String species;

    @Size(max = 512, message = "头像URL长度不能超过 512 个字符")
    private String avatarUrl;

    @Size(max = 2000, message = "简介长度不能超过 2000 个字符")
    private String description;

    @Size(max = 32, message = "首次出场章节长度不能超过 32 个字符")
    private String firstChapter;

    @Size(max = 32, message = "战力等级长度不能超过 32 个字符")
    private String powerLevel;

    /** 排序权重 */
    private Integer sortOrder;
}
