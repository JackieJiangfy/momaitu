package com.novelgraph.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统统一响应码
 *
 * @author novelgraph
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    INTERNAL_ERROR(500, "系统内部错误"),

    /* 业务错误码 1xxxx */
    USERNAME_ALREADY_EXISTS(10001, "用户名已被占用"),
    USERNAME_OR_PASSWORD_ERROR(10002, "用户名或密码错误"),
    USER_DISABLED(10003, "账号已被禁用"),
    OLD_PASSWORD_ERROR(10004, "原密码错误"),

    NOVEL_NOT_FOUND(20001, "小说不存在"),
    NOVEL_ACCESS_DENIED(20002, "无权访问该小说"),

    CHARACTER_NOT_FOUND(30001, "角色不存在"),
    CHARACTER_NAME_DUPLICATE(30002, "角色名称已存在"),

    RELATIONSHIP_NOT_FOUND(40001, "关系不存在"),
    RELATIONSHIP_DUPLICATE(40002, "该关系已存在"),
    RELATIONSHIP_TYPE_NOT_FOUND(40003, "关系类型不存在");

    private final int code;
    private final String message;
}
