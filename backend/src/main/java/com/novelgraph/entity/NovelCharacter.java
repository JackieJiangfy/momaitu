package com.novelgraph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 角色实体（对应 novel_character 表）
 *
 * @author novelgraph
 */
@Data
@TableName(value = "novel_character", autoResultMap = true)
public class NovelCharacter implements Serializable {

    /** 主键，UUID */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属小说ID */
    private String novelId;

    /** 角色名称 */
    private String name;

    /** 别名/称号，逗号分隔 */
    private String alias;

    /** 所属势力/门派/阵营 */
    private String faction;

    /** 定位：主角/配角/反派/路人 */
    private String roleType;

    /** 种族：人/仙/魔/妖/其他 */
    private String species;

    /** 角色头像URL */
    private String avatarUrl;

    /** 角色简介 */
    private String description;

    /** 首次出场章节 */
    private String firstChapter;

    /** 战力等级（自定义） */
    private String powerLevel;

    /** 排序权重 */
    private Integer sortOrder;

    // ===== 墨流扩展字段（NULL 默认，向后兼容） =====

    /** 一句话定位（墨流 one_line_pitch） */
    private String oneLinePitch;

    /** 说话风格（墨流 SpeechProfile，JSON） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> speechProfile;

    /** 说话样本列表（墨流） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> speechSamples;

    /** 内心戏风格（墨流） */
    private String innerVoiceStyle;

    /** 核心欲望（墨流 core.core_desire） */
    private String coreDesire;

    /** 表层欲望（墨流 core.surface_desire） */
    private String surfaceDesire;

    /** 深层恐惧（墨流 core.deep_fear） */
    private String deepFear;

    /** 价值观底线列表（墨流） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> valueBottomLine;

    /** 背景摘要（墨流） */
    private String backstorySummary;

    /** 背景对性格的影响（墨流） */
    private String backstoryImpact;

    /** 隐藏线索列表（墨流） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> hiddenClues;

    /** 角色状态：active/injured/missing/dead/left */
    private String status;

    /** 当前位置（墨流 state.location） */
    private String currentLocation;

    /** 当前目标（墨流 state.current_goal） */
    private String currentGoal;

    /** 当前情绪（墨流 state.current_emotion） */
    private String currentEmotion;

    /** 身体状态（墨流 state.physical_state） */
    private String physicalState;

    /** 持有资源列表（墨流） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> resources;

    /** 已知信息列表（墨流） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> knownInfo;

    /** 外观详情（墨流 Appearance，JSON） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> appearance;

    /** 最近一次墨流同步时间 */
    private LocalDateTime moliuSyncedAt;

    /** 逻辑删除：0未删 1已删 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
