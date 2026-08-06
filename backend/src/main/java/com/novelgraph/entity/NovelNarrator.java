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
 * 小说叙述者实体（对应 novel_narrator 表，1:1 关联小说）
 * 来源：墨流 NarratorCard
 *
 * @author novelgraph
 */
@Data
@TableName(value = "novel_narrator", autoResultMap = true)
public class NovelNarrator implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属小说ID（1:1） */
    private String novelId;

    /** 叙述者名 */
    private String name;

    /** 定位 */
    private String oneLinePitch;

    /** 视角：全知/限知/第一人称 */
    private String perspective;

    /** 语言风格 */
    private String languageStyle;

    /** 日常语气 */
    private String dailyTone;

    /** 高潮语气 */
    private String climaxTone;

    /** 情绪戏语气 */
    private String emotionalTone;

    /** 句式特征列表 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> sentenceFeatures;

    /** 禁用套话列表 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> bannedPhrases;

    /** 日常样本 */
    private String samplesDaily;

    /** 高潮样本 */
    private String samplesClimax;

    /** 情绪样本 */
    private String samplesEmotional;

    /** 原始 Markdown 全文（备份） */
    private String rawMarkdown;

    /** 墨流同步时间 */
    private LocalDateTime moliuSyncedAt;
}
