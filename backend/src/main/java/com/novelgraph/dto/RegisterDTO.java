package com.novelgraph.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 注册请求 DTO
 *
 * @author novelgraph
 */
@Data
public class RegisterDTO implements Serializable {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 64, message = "用户名长度需为 3-64 个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度需为 6-32 个字符")
    private String password;

    @Size(max = 64, message = "昵称长度不能超过 64 个字符")
    private String nickname;
}
