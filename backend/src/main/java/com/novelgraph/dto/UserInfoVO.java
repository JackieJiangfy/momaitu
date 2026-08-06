package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息响应 VO（不含密码）
 *
 * @author novelgraph
 */
@Data
public class UserInfoVO implements Serializable {

    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private String status;
    private LocalDateTime createdAt;
}
