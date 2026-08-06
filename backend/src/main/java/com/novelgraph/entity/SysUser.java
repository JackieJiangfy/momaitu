package com.novelgraph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体（对应 sys_user 表）
 *
 * @author novelgraph
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    /** 主键，UUID */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 用户名 */
    private String username;

    /** 加密密码（BCrypt） */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 状态：ACTIVE/DISABLED */
    private String status;

    /** 逻辑删除：0未删 1已删 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
