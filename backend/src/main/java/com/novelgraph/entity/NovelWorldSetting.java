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
 * 小说世界观实体（对应 novel_world_setting 表，1:1 关联小说）
 * 来源：墨流 WorldSetting
 *
 * @author novelgraph
 */
@Data
@TableName(value = "novel_world_setting", autoResultMap = true)
public class NovelWorldSetting implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属小说ID（1:1） */
    private String novelId;

    /** 时代背景（墨流 era） */
    private String era;

    /** 核心规则列表（墨流 core_rules） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> coreRules;

    /** 力量体系（墨流 power_system） */
    private String powerSystem;

    /** 势力概况（墨流 faction_summary） */
    private String factionSummary;

    /** 硬约束列表（墨流 key_constraints） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> keyConstraints;

    /** 叙事基调（墨流 narrative_style） */
    private String narrativeStyle;

    /** 原始 YAML 全文（备份） */
    private String rawYaml;

    /** 墨流同步时间 */
    private LocalDateTime moliuSyncedAt;
}
