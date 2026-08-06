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
 * 角色状态历史实体（对应 novel_character_state_history 表）
 * 每章保存一次角色状态快照，用于时间轴可视化
 *
 * @author novelgraph
 */
@Data
@TableName(value = "novel_character_state_history", autoResultMap = true)
public class NovelCharacterStateHistory implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属小说 */
    private String novelId;

    /** 角色ID */
    private String characterId;

    /** 章节号（这一章后的状态） */
    private Integer chapterNum;

    /** 状态：active/injured/missing/dead/left */
    private String status;

    /** 位置 */
    private String location;

    /** 当前目标 */
    private String currentGoal;

    /** 当前情绪 */
    private String currentEmotion;

    /** 身体状态 */
    private String physicalState;

    /** 持有资源 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> resources;

    /** 已知信息 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> knownInfo;

    /** 原始角色卡 YAML 快照 */
    private String snapshotYaml;

    /** 同步时间 */
    private LocalDateTime syncedAt;
}
