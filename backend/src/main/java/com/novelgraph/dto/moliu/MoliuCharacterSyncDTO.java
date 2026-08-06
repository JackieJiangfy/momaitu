package com.novelgraph.dto.moliu;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 墨流角色同步 DTO
 * 字段名遵循墨流 CharacterCard Pydantic 模型（snake_case），
 * 通过 @JsonNaming 自动映射到 Java 驼峰。
 *
 * @author novelgraph
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoliuCharacterSyncDTO implements Serializable {

    /** 角色名（必填，作为同一小说内 upsert 唯一键） */
    private String name;

    /** 一句话定位 */
    private String oneLinePitch;

    /** 说话风格：{style, sentence_length, tone, common_words[], banned_words[]} */
    private Map<String, Object> speechProfile;

    /** 说话样本列表 */
    private List<String> speechSamples;

    /** 内心戏风格 */
    private String innerVoiceStyle;

    /** 核心：{core_desire, surface_desire, deep_fear, value_bottom_line[]} */
    private Map<String, Object> core;

    /** 背景摘要 */
    private String backstorySummary;

    /** 背景对性格的影响 */
    private String backstoryImpact;

    /** 隐藏线索列表 */
    private List<String> hiddenClues;

    /** 当前状态：{status, location, current_goal, current_emotion, physical_state, resources[], known_info[]} */
    private Map<String, Object> state;

    /** 外观：{height, build, face, hair, typical_outfit, signature_gesture} */
    private Map<String, Object> appearance;
}
