package com.novelgraph.service;

import com.novelgraph.dto.*;

/**
 * 认证服务接口
 *
 * @author novelgraph
 */
public interface AuthService {

    /**
     * 注册
     *
     * @return 用户信息
     */
    UserInfoVO register(RegisterDTO dto);

    /**
     * 登录，返回 Sa-Token
     *
     * @return token 字符串
     */
    String login(LoginDTO dto);

    /**
     * 获取当前登录用户信息
     */
    UserInfoVO getCurrentUserInfo();

    /**
     * 修改密码
     */
    void changePassword(ChangePasswordDTO dto);

    /**
     * 修改个人信息（昵称/头像）
     */
    UserInfoVO updateProfile(UpdateProfileDTO dto);
}
