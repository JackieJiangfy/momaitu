package com.novelgraph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关系抽取任务实体（对应 novel_relation_extract_task 表）
 * 用于追踪从章节正文抽取角色关系的 LLM 任务状态
 *
 * @author novelgraph
 */
@Data
@TableName("novel_relation_extract_task")
public class NovelRelationExtractTask implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属小说 */
    private String novelId;

    /** 章节号（NULL 表示全本） */
    private Integer chapterNum;

    /** 模式：incremental/full */
    private String mode;

    /** 状态：pending/processing/done/failed */
    private String status;

    /** 完成时间 */
    private LocalDateTime extractedAt;

    /** 抽取到的关系数 */
    private Integer relationsFound;

    /** 错误信息 */
    private String errorMsg;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
