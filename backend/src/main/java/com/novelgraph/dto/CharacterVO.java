package com.novelgraph.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 角色响应 VO
 *
 * @author novelgraph
 */
@Data
public class CharacterVO implements Serializable {

    private String id;
    private String novelId;
    private String name;
    private String alias;
    private String faction;
    private String roleType;
    private String species;
    private String avatarUrl;
    private String description;
    private String firstChapter;
    private String powerLevel;
    private Integer sortOrder;
    /** 关联关系数（用于图谱节点大小计算） */
    private Integer relationCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===== 墨流扩展字段（NULL 默认，向后兼容） =====

    /** 一句话定位（墨流 one_line_pitch） */
    private String oneLinePitch;

    /** 说话风格（墨流 SpeechProfile，JSON） */
    private Map<String, Object> speechProfile;

    /** 说话样本列表（墨流） */
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
    private List<String> valueBottomLine;

    /** 背景摘要（墨流） */
    private String backstorySummary;

    /** 背景对性格的影响（墨流） */
    private String backstoryImpact;

    /** 隐藏线索列表（墨流） */
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
    private List<String> resources;

    /** 已知信息列表（墨流） */
    private List<String> knownInfo;

    /** 外观详情（墨流 Appearance，JSON） */
    private Map<String, Object> appearance;

    /** 最近一次墨流同步时间 */
    private LocalDateTime moliuSyncedAt;
}
