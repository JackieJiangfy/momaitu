package com.novelgraph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关系类型配置实体（对应 novel_rel_type_config 表）
 * 系统模板：novel_id = 'SYSTEM_PRESET'
 * 业务数据：novel_id = 具体小说ID（新建小说时从模板复制）
 *
 * @author novelgraph
 */
@Data
@TableName("novel_rel_type_config")
public class NovelRelTypeConfig implements Serializable {

    /** 主键，UUID */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属小说ID（SYSTEM_PRESET 表示系统模板） */
    private String novelId;

    /** 关系类型名称 */
    private String typeName;

    /** 关系性质：positive/neutral/negative */
    private String category;

    /** 图标标识（可选） */
    private String icon;

    /** 排序权重 */
    private Integer sortOrder;

    /** 逻辑删除：0未删 1已删 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
