package com.novelgraph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色实体（对应 novel_character 表）
 *
 * @author novelgraph
 */
@Data
@TableName("novel_character")
public class NovelCharacter implements Serializable {

    /** 主键，UUID */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属小说ID */
    private String novelId;

    /** 角色名称 */
    private String name;

    /** 别名/称号，逗号分隔 */
    private String alias;

    /** 所属势力/门派/阵营 */
    private String faction;

    /** 定位：主角/配角/反派/路人 */
    private String roleType;

    /** 种族：人/仙/魔/妖/其他 */
    private String species;

    /** 角色头像URL */
    private String avatarUrl;

    /** 角色简介 */
    private String description;

    /** 首次出场章节 */
    private String firstChapter;

    /** 战力等级（自定义） */
    private String powerLevel;

    /** 排序权重 */
    private Integer sortOrder;

    /** 逻辑删除：0未删 1已删 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
