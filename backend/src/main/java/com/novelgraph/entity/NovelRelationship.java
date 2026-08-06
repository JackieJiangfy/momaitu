package com.novelgraph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关系实体（对应 novel_relationship 表）
 *
 * @author novelgraph
 */
@Data
@TableName("novel_relationship")
public class NovelRelationship implements Serializable {

    /** 主键，UUID */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属小说ID */
    private String novelId;

    /** 源角色ID */
    private String sourceId;

    /** 目标角色ID */
    private String targetId;

    /** 关系类型：父子/师徒/恋人/仇敌... */
    private String relType;

    /** 关系性质：positive/neutral/negative */
    private String category;

    /** 是否单向：0双向 1单向（如暗恋） */
    private Integer directed;

    /** 关系强度：1-10 */
    private Integer intensity;

    /** 关系说明 */
    private String description;

    /** 关系形成章节 */
    private String startChapter;

    /** 关系结束章节 */
    private String endChapter;

    /** 逻辑删除：0未删 1已删 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
