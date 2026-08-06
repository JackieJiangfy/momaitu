package com.novelgraph.dto.moliu;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 墨流叙述者同步 DTO
 * 对应墨流 NarratorCard Pydantic 模型。
 *
 * @author novelgraph
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoliuNarratorSyncDTO implements Serializable {

    /** 叙述者名 */
    private String name;

    /** 一句话定位 */
    private String oneLinePitch;

    /** 日常语气 */
    private String dailyTone;

    /** 高潮语气 */
    private String climaxTone;

    /** 情绪戏语气 */
    private String emotionalTone;

    /** 句式特征列表 */
    private List<String> sentenceFeatures;

    /** 禁用套话列表 */
    private List<String> bannedPhrases;

    /** 日常场景样本 */
    private String samplesDaily;

    /** 高潮场景样本 */
    private String samplesClimax;

    /** 情绪戏样本 */
    private String samplesEmotional;

    /** 视角定位（全知/限知/第一人称等） */
    private String perspective;

    /** 语言风格 */
    private String languageStyle;

    /** 原始 Markdown 全文（备份） */
    private String rawMarkdown;
}
