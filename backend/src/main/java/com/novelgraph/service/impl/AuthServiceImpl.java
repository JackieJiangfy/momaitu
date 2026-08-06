package com.novelgraph.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novelgraph.common.BusinessException;
import com.novelgraph.common.ResultCode;
import com.novelgraph.dto.*;
import com.novelgraph.entity.SysUser;
import com.novelgraph.mapper.SysUserMapper;
import com.novelgraph.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务实现
 * - 密码加密：BCrypt
 * - 登录态：Sa-Token (StpUtil)
 *
 * @author novelgraph
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO register(RegisterDTO dto) {
        // 用户名唯一性校验
        Long exists = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (exists != null && exists > 0) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXISTS);
        }

        // 构建用户实体
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setStatus("ACTIVE");

        userMapper.insert(user);
        log.info("用户注册成功: id={}, username={}", user.getId(), user.getUsername());

        return toVO(user);
    }

    @Override
    public String login(LoginDTO dto) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // Sa-Token 登录（使用用户 ID 作为登录标识）
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();
        log.info("用户登录成功: id={}, username={}, token={}", user.getId(), user.getUsername(), token);
        return token;
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        StpUtil.checkLogin();
        String userId = StpUtil.getLoginIdAsString();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(ChangePasswordDTO dto) {
        StpUtil.checkLogin();
        String userId = StpUtil.getLoginIdAsString();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);
        log.info("用户修改密码成功: id={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO updateProfile(UpdateProfileDTO dto) {
        StpUtil.checkLogin();
        String userId = StpUtil.getLoginIdAsString();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        userMapper.updateById(user);
        log.info("用户更新资料成功: id={}", userId);
        return toVO(user);
    }

    /**
     * 实体转 VO（不暴露密码）
     */
    private UserInfoVO toVO(SysUser user) {
        UserInfoVO vo = new UserInfoVO();
        BeanUtil.copyProperties(user, vo);
        return vo;
    }
}
