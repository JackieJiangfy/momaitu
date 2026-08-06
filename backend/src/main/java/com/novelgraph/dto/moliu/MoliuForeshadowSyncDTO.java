package com.novelgraph.dto.moliu;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 墨流伏笔同步 DTO
 * 对应墨流 ForeshadowEntry 数据结构。
 *
 * @author novelgraph
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoliuForeshadowSyncDTO implements Serializable {

    /** 墨流伏笔ID（f001 等，作为 upsert 唯一键） */
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

    /** 关联角色名列表（同步时由 Service 转换为 characterId） */
    private List<String> relatedCharacters;
}
