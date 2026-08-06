package com.novelgraph.dto.moliu;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;

/**
 * 墨流关系同步 DTO
 * 字段名遵循墨流 snake_case，通过 @JsonNaming 自动映射到 Java 驼峰。
 *
 * 关键字段：
 * - source_name / target_name：用角色名而非 ID（墨流 LLM 抽取时只知道名字）
 * - 后端根据 (novelId, name) 查找角色 ID，找不到则跳过该关系
 *
 * @author novelgraph
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoliuRelationshipSyncDTO implements Serializable {

    /** 源角色名（必填） */
    private String sourceName;

    /** 目标角色名（必填） */
    private String targetName;

    /** 关系类型：父子/师徒/恋人/仇敌... */
    private String relType;

    /** 关系性质：positive/neutral/negative */
    private String category;

    /** 是否单向：0双向 1单向（如暗恋）。为空默认 0 */
    private Integer directed;

    /** 关系强度：1-10，为空默认 5 */
    private Integer intensity;

    /** 关系说明 */
    private String description;

    /** 关系形成章节（如 "第3章"） */
    private String startChapter;
}
