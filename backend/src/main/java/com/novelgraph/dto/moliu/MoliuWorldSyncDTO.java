package com.novelgraph.dto.moliu;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 墨流世界观同步 DTO
 * 对应墨流 WorldSetting Pydantic 模型。
 *
 * @author novelgraph
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoliuWorldSyncDTO implements Serializable {

    /** 时代背景 */
    private String era;

    /** 核心规则列表 */
    private List<String> coreRules;

    /** 力量体系 */
    private String powerSystem;

    /** 势力概况 */
    private String factionSummary;

    /** 硬约束列表 */
    private List<String> keyConstraints;

    /** 叙事基调 */
    private String narrativeStyle;

    /** 原始 YAML 全文（备份） */
    private String rawYaml;
}
