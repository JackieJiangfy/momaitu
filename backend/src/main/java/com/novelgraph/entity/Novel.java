package com.novelgraph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 小说实体（对应 novel 表）
 *
 * @author novelgraph
 */
@Data
@TableName("novel")
public class Novel implements Serializable {

    /** 主键，UUID */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属用户ID */
    private String userId;

    /** 小说名称 */
    private String title;

    /** 作者 */
    private String author;

    /** 简介 */
    private String description;

    /** 封面图片URL */
    private String coverUrl;

    /** 状态：ACTIVE/ARCHIVED */
    private String status;

    /** 逻辑删除：0未删 1已删 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
