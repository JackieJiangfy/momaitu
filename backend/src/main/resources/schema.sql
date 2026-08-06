-- ============================================================
-- 小说角色关系图谱系统 - 数据库建表脚本
-- 数据库：novel_graph (MySQL 8.0)
-- 字符集：utf8mb4
-- 说明：在原设计文档 DDL 基础上，为业务表补充 deleted 逻辑删除字段
-- ============================================================

CREATE DATABASE IF NOT EXISTS novel_graph
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE novel_graph;

-- 删除已存在的表（按外键依赖反向顺序）
DROP TABLE IF EXISTS novel_rel_type_config;
DROP TABLE IF EXISTS novel_relationship;
DROP TABLE IF EXISTS novel_character;
DROP TABLE IF EXISTS novel;
DROP TABLE IF EXISTS sys_user;

-- ==================== 1. 用户表 ====================
CREATE TABLE sys_user (
  id          VARCHAR(32)  NOT NULL                COMMENT '主键，UUID',
  username    VARCHAR(64)  NOT NULL                COMMENT '用户名',
  password    VARCHAR(128) NOT NULL                COMMENT '加密密码（BCrypt）',
  nickname    VARCHAR(64)  DEFAULT NULL            COMMENT '昵称',
  avatar      VARCHAR(512) DEFAULT NULL            COMMENT '头像URL',
  status      VARCHAR(16)  DEFAULT 'ACTIVE'        COMMENT 'ACTIVE/DISABLED',
  deleted     TINYINT      DEFAULT 0               COMMENT '逻辑删除：0未删 1已删',
  created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ==================== 2. 小说表 ====================
CREATE TABLE novel (
  id          VARCHAR(32)  NOT NULL                COMMENT '主键，UUID',
  user_id     VARCHAR(32)  NOT NULL                COMMENT '所属用户ID',
  title       VARCHAR(128) NOT NULL                COMMENT '小说名称',
  author      VARCHAR(64)  DEFAULT NULL            COMMENT '作者',
  description TEXT         DEFAULT NULL             COMMENT '简介',
  cover_url   VARCHAR(512) DEFAULT NULL             COMMENT '封面图片URL',
  status      VARCHAR(16)  DEFAULT 'ACTIVE'         COMMENT 'ACTIVE/ARCHIVED',
  deleted     TINYINT      DEFAULT 0                COMMENT '逻辑删除：0未删 1已删',
  created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_user_id (user_id),
  INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小说表';

-- ==================== 3. 角色表 ====================
CREATE TABLE novel_character (
  id            VARCHAR(32)  NOT NULL              COMMENT '主键，UUID',
  novel_id      VARCHAR(32)  NOT NULL              COMMENT '所属小说ID',
  name          VARCHAR(64)  NOT NULL              COMMENT '角色名称',
  alias         VARCHAR(256) DEFAULT NULL         COMMENT '别名/称号，逗号分隔',
  faction       VARCHAR(64)  DEFAULT NULL          COMMENT '所属势力/门派/阵营',
  role_type     VARCHAR(32)  DEFAULT NULL          COMMENT '定位：主角/配角/反派/路人',
  species       VARCHAR(32)  DEFAULT NULL           COMMENT '种族：人/仙/魔/妖/其他',
  avatar_url    VARCHAR(512) DEFAULT NULL          COMMENT '角色头像URL',
  description   TEXT         DEFAULT NULL          COMMENT '角色简介',
  first_chapter VARCHAR(32)  DEFAULT NULL          COMMENT '首次出场章节',
  power_level   VARCHAR(32)  DEFAULT NULL          COMMENT '战力等级（自定义）',
  sort_order    INT          DEFAULT 0             COMMENT '排序权重',
  deleted       TINYINT      DEFAULT 0             COMMENT '逻辑删除：0未删 1已删',
  created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_novel_id (novel_id),
  INDEX idx_name (name),
  INDEX idx_novel_deleted (novel_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ==================== 4. 关系表 ====================
CREATE TABLE novel_relationship (
  id            VARCHAR(32)  NOT NULL              COMMENT '主键，UUID',
  novel_id      VARCHAR(32)  NOT NULL              COMMENT '所属小说ID',
  source_id     VARCHAR(32)  NOT NULL              COMMENT '源角色ID',
  target_id     VARCHAR(32)  NOT NULL              COMMENT '目标角色ID',
  rel_type      VARCHAR(32)  NOT NULL              COMMENT '关系类型：父子/师徒/恋人/仇敌...',
  category      VARCHAR(16)  NOT NULL              COMMENT '关系性质：positive/neutral/negative',
  directed      TINYINT      DEFAULT 0             COMMENT '是否单向：0双向 1单向（如暗恋）',
  intensity     TINYINT      DEFAULT 5             COMMENT '关系强度：1-10',
  description   VARCHAR(256) DEFAULT NULL           COMMENT '关系说明',
  start_chapter VARCHAR(32)  DEFAULT NULL          COMMENT '关系形成章节',
  end_chapter   VARCHAR(32)  DEFAULT NULL          COMMENT '关系结束章节',
  deleted       TINYINT      DEFAULT 0             COMMENT '逻辑删除：0未删 1已删',
  created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_novel_id (novel_id),
  INDEX idx_source (source_id),
  INDEX idx_target (target_id),
  INDEX idx_novel_deleted (novel_id, deleted),
  UNIQUE KEY uk_relation (novel_id, source_id, target_id, rel_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关系表';

-- ==================== 5. 关系类型配置表 ====================
CREATE TABLE novel_rel_type_config (
  id         VARCHAR(32) NOT NULL                   COMMENT '主键，UUID',
  novel_id   VARCHAR(32) NOT NULL                   COMMENT '所属小说ID',
  type_name  VARCHAR(32) NOT NULL                   COMMENT '关系类型名称',
  category   VARCHAR(16) NOT NULL                   COMMENT '关系性质：positive/neutral/negative',
  icon       VARCHAR(32) DEFAULT NULL               COMMENT '图标标识（可选）',
  sort_order INT         DEFAULT 0                  COMMENT '排序权重',
  deleted    TINYINT     DEFAULT 0                  COMMENT '逻辑删除：0未删 1已删',
  created_at DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  INDEX idx_novel_id (novel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关系类型配置';
