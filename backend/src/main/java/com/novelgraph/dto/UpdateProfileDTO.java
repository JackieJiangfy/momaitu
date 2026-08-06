package com.novelgraph.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 修改个人信息请求 DTO（仅允许改昵称和头像）
 *
 * @author novelgraph
 */
@Data
public class UpdateProfileDTO implements Serializable {

    @Size(max = 64, message = "昵称长度不能超过 64 个字符")
    private String nickname;

    @Size(max = 512, message = "头像URL长度不能超过 512 个字符")
    private String avatar;
}
