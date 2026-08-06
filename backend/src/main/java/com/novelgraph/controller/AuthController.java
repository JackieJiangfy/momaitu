package com.novelgraph.controller;

import com.novelgraph.common.Result;
import com.novelgraph.dto.*;
import com.novelgraph.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 路径前缀：/auth
 * 在 Sa-Token 拦截器中放行 /auth/register 和 /auth/login
 *
 * @author novelgraph
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     * POST /auth/register
     */
    @PostMapping("/register")
    public Result<UserInfoVO> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success(authService.register(dto));
    }

    /**
     * 登录
     * POST /auth/login
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        String token = authService.login(dto);
        Map<String, Object> data = new HashMap<>(2);
        data.put("token", token);
        data.put("tokenName", "Authorization");
        return Result.success(data);
    }

    /**
     * 获取当前用户信息
     * GET /auth/info
     */
    @GetMapping("/info")
    public Result<UserInfoVO> info() {
        return Result.success(authService.getCurrentUserInfo());
    }

    /**
     * 修改密码
     * PUT /auth/password
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        authService.changePassword(dto);
        return Result.success();
    }

    /**
     * 修改个人信息
     * PUT /auth/profile
     */
    @PutMapping("/profile")
    public Result<UserInfoVO> updateProfile(@Valid @RequestBody UpdateProfileDTO dto) {
        return Result.success(authService.updateProfile(dto));
    }
}
