package com.novelgraph.dto.moliu;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 墨流章节同步 DTO
 * 对应墨流 ChapterResult + ChapterMeta + RhythmRecord + QualityReport 合并字段。
 *
 * @author novelgraph
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoliuChapterSyncDTO implements Serializable {

    /** 章节号（必填，作为 upsert 唯一键） */
    private Integer chapterNum;

    /** 章节标题 */
    private String title;

    /** 章节正文 */
    private String content;

    /** 字数 */
    private Integer wordCount;

    /** 消耗 token */
    private Integer tokensUsed;

    /** 使用的模型 */
    private String modelUsed;

    /** 本章情绪 */
    private String emotion;

    /** 章节摘要 */
    private String summary;

    /** 出场角色名列表 */
    private List<String> keyCharacters;

    /** 关键事件列表 */
    private List<String> keyEvents;

    /** 首句 */
    private String firstSentence;

    /** 尾句 */
    private String lastSentence;

    /** 章节类型：normal/opening/setup/climax/transition/epilogue */
    private String chapterType;

    /** 张力评分 1-10 */
    private Integer tensionScore;

    /** 开场风格 */
    private String openingStyle;

    /** 收尾风格 */
    private String closingStyle;

    /** 对话占比 */
    private BigDecimal dialogueRatio;

    /** 是否有记忆点 */
    private Integer hasMemorable;

    /** 一致性致命错误数 */
    private Integer consistencyFatal;

    /** 一致性警告数 */
    private Integer consistencyWarn;

    /** 读者想看下一章 */
    private Integer readerWantNext;

    /** 原始 meta.json 备份 */
    private String rawMetaJson;
}
