package com.novelgraph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 小说伏笔实体（对应 novel_foreshadow 表）
 * 来源：墨流 ForeshadowEntry
 *
 * @author novelgraph
 */
@Data
@TableName(value = "novel_foreshadow", autoResultMap = true)
public class NovelForeshadow implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属小说 */
    private String novelId;

    /** 墨流伏笔ID (f001 等) */
    private String moliuId;

    /** 伏笔描述 */
    private String description;

    /** 状态：planted/building/paid/dropped */
    private String status;

    /** 优先级：high/normal/low */
    private String priority;

    /** 类型：明/暗/潜 */
    private String type;

    /** 埋入章节 */
    private Integer plantedChapter;

    /** 最近推进章节 */
    private Integer lastAdvanced;

    /** 回收章节 */
    private Integer paidChapter;

    /** 关联角色ID列表 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> relatedCharacters;

    /** 墨流同步时间 */
    private LocalDateTime moliuSyncedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
