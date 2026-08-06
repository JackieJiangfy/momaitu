package com.novelgraph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 小说章节实体（对应 novel_chapter 表）
 * 来源：墨流 ChapterResult + ChapterMeta + RhythmRecord + QualityReport
 *
 * @author novelgraph
 */
@Data
@TableName(value = "novel_chapter", autoResultMap = true)
public class NovelChapter implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属小说ID */
    private String novelId;

    /** 章节号（墨流 chapter_num） */
    private Integer chapterNum;

    /** 章节标题 */
    private String title;

    /** 章节正文（墨流 ChapterResult.content） */
    private String content;

    /** 字数 */
    private Integer wordCount;

    /** 消耗 token */
    private Integer tokensUsed;

    /** 使用的模型 */
    private String modelUsed;

    /** 本章情绪 */
    private String emotion;

    /** 章节摘要（墨流 ChapterMeta.summary） */
    private String summary;

    /** 出场角色名列表（墨流 key_characters） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> keyCharacters;

    /** 关键事件列表（墨流 key_events） */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> keyEvents;

    /** 首句 */
    private String firstSentence;

    /** 尾句 */
    private String lastSentence;

    /** 章节类型：normal/opening/setup/climax/transition/epilogue */
    private String chapterType;

    /** 张力评分 1-10（墨流 RhythmRecord.tension_score） */
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

    /** 墨流同步时间 */
    private LocalDateTime moliuSyncedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
