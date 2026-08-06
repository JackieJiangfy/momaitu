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
DROP TABLE IF EXISTS novel_relation_extract_task;
DROP TABLE IF EXISTS novel_foreshadow;
DROP TABLE IF EXISTS novel_character_state_history;
DROP TABLE IF EXISTS novel_chapter;
DROP TABLE IF EXISTS novel_narrator;
DROP TABLE IF EXISTS novel_world_setting;
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
-- 注：扩展字段用于承接墨流 CharacterCard，均为 NULL 默认，向后兼容旧数据
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
  -- 以下为墨流扩展字段（NULL 默认，旧数据无需迁移）
  one_line_pitch     VARCHAR(256) DEFAULT NULL     COMMENT '一句话定位（墨流）',
  speech_profile     JSON         DEFAULT NULL     COMMENT '说话风格 SpeechProfile（墨流）',
  speech_samples     JSON         DEFAULT NULL     COMMENT '说话样本列表（墨流）',
  inner_voice_style  VARCHAR(512) DEFAULT NULL    COMMENT '内心戏风格（墨流）',
  core_desire        VARCHAR(256) DEFAULT NULL     COMMENT '核心欲望（墨流 core.core_desire）',
  surface_desire     VARCHAR(256) DEFAULT NULL     COMMENT '表层欲望（墨流 core.surface_desire）',
  deep_fear          VARCHAR(256) DEFAULT NULL     COMMENT '深层恐惧（墨流 core.deep_fear）',
  value_bottom_line  JSON         DEFAULT NULL     COMMENT '价值观底线列表（墨流）',
  backstory_summary  TEXT         DEFAULT NULL     COMMENT '背景摘要（墨流）',
  backstory_impact   VARCHAR(512) DEFAULT NULL     COMMENT '背景对性格的影响（墨流）',
  hidden_clues       JSON         DEFAULT NULL     COMMENT '隐藏线索列表（墨流）',
  status             VARCHAR(16)  DEFAULT 'active' COMMENT '角色状态：active/injured/missing/dead/left',
  current_location   VARCHAR(128) DEFAULT NULL     COMMENT '当前位置（墨流 state.location）',
  current_goal       VARCHAR(256) DEFAULT NULL     COMMENT '当前目标（墨流 state.current_goal）',
  current_emotion    VARCHAR(64)  DEFAULT NULL     COMMENT '当前情绪（墨流 state.current_emotion）',
  physical_state     VARCHAR(128) DEFAULT NULL     COMMENT '身体状态（墨流 state.physical_state）',
  resources          JSON         DEFAULT NULL     COMMENT '持有资源列表（墨流）',
  known_info         JSON         DEFAULT NULL     COMMENT '已知信息列表（墨流）',
  appearance         JSON         DEFAULT NULL     COMMENT '外观详情 height/build/face/hair/outfit/gesture（墨流）',
  moliu_synced_at    DATETIME     DEFAULT NULL    COMMENT '最近一次墨流同步时间',
  deleted       TINYINT      DEFAULT 0             COMMENT '逻辑删除：0未删 1已删',
  created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  INDEX idx_novel_id (novel_id),
  INDEX idx_name (name),
  INDEX idx_novel_deleted (novel_id, deleted),
  INDEX idx_moliu_synced (moliu_synced_at),
  UNIQUE KEY uk_novel_name (novel_id, name, deleted)  -- 同一小说内角色名唯一，支持墨流 upsert
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

-- ==================== 6. 世界观表（墨流同步） ====================
CREATE TABLE novel_world_setting (
  id              VARCHAR(32)  NOT NULL              COMMENT '主键，UUID',
  novel_id        VARCHAR(32)  NOT NULL              COMMENT '所属小说ID（1:1）',
  era             VARCHAR(128) DEFAULT NULL          COMMENT '时代背景（墨流 era）',
  core_rules      JSON         DEFAULT NULL           COMMENT '核心规则列表（墨流 core_rules）',
  power_system    VARCHAR(512) DEFAULT NULL           COMMENT '力量体系（墨流 power_system）',
  faction_summary VARCHAR(512) DEFAULT NULL          COMMENT '势力概况（墨流 faction_summary）',
  key_constraints JSON         DEFAULT NULL           COMMENT '硬约束列表（墨流 key_constraints）',
  narrative_style VARCHAR(256) DEFAULT NULL           COMMENT '叙事基调（墨流 narrative_style）',
  raw_yaml        TEXT         DEFAULT NULL            COMMENT '原始 YAML 全文（备份）',
  moliu_synced_at DATETIME    DEFAULT NULL            COMMENT '墨流同步时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_novel (novel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小说世界观（来自墨流）';

-- ==================== 7. 叙述者表（墨流同步） ====================
CREATE TABLE novel_narrator (
  id                VARCHAR(32)  NOT NULL            COMMENT '主键，UUID',
  novel_id          VARCHAR(32)  NOT NULL            COMMENT '所属小说ID（1:1）',
  name              VARCHAR(64)  DEFAULT '叙述者'      COMMENT '叙述者名',
  one_line_pitch    VARCHAR(256) DEFAULT NULL         COMMENT '定位',
  perspective       VARCHAR(64)  DEFAULT NULL          COMMENT '视角：全知/限知/第一人称',
  language_style    VARCHAR(128) DEFAULT NULL          COMMENT '语言风格',
  daily_tone        TEXT         DEFAULT NULL          COMMENT '日常语气',
  climax_tone       TEXT         DEFAULT NULL          COMMENT '高潮语气',
  emotional_tone    TEXT         DEFAULT NULL          COMMENT '情绪戏语气',
  sentence_features JSON         DEFAULT NULL          COMMENT '句式特征列表',
  banned_phrases    JSON         DEFAULT NULL          COMMENT '禁用套话列表',
  samples_daily     TEXT         DEFAULT NULL          COMMENT '日常样本',
  samples_climax    TEXT         DEFAULT NULL          COMMENT '高潮样本',
  samples_emotional TEXT         DEFAULT NULL          COMMENT '情绪样本',
  raw_markdown      TEXT         DEFAULT NULL          COMMENT '原始 Markdown 全文（备份）',
  moliu_synced_at   DATETIME    DEFAULT NULL           COMMENT '墨流同步时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_novel (novel_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小说叙述者（来自墨流）';

-- ==================== 8. 章节表（墨流同步） ====================
CREATE TABLE novel_chapter (
  id                VARCHAR(32)  NOT NULL             COMMENT '主键，UUID',
  novel_id          VARCHAR(32)  NOT NULL             COMMENT '所属小说ID',
  chapter_num       INT          NOT NULL              COMMENT '章节号（墨流 chapter_num）',
  title             VARCHAR(128) DEFAULT NULL          COMMENT '章节标题',
  content           MEDIUMTEXT   DEFAULT NULL          COMMENT '章节正文（墨流 ChapterResult.content）',
  word_count        INT          DEFAULT 0             COMMENT '字数',
  tokens_used       INT          DEFAULT 0             COMMENT '消耗 token',
  model_used        VARCHAR(64)  DEFAULT NULL          COMMENT '使用的模型',
  emotion           VARCHAR(32)  DEFAULT NULL          COMMENT '本章情绪',
  summary           TEXT         DEFAULT NULL          COMMENT '章节摘要（墨流 ChapterMeta.summary）',
  key_characters    JSON         DEFAULT NULL           COMMENT '出场角色名列表（墨流 key_characters）',
  key_events        JSON         DEFAULT NULL           COMMENT '关键事件列表（墨流 key_events）',
  first_sentence    VARCHAR(512) DEFAULT NULL          COMMENT '首句',
  last_sentence     VARCHAR(512) DEFAULT NULL          COMMENT '尾句',
  chapter_type      VARCHAR(32)  DEFAULT 'normal'      COMMENT '章节类型：normal/opening/setup/climax/transition/epilogue',
  tension_score     INT          DEFAULT 5             COMMENT '张力评分 1-10（墨流 RhythmRecord）',
  opening_style     VARCHAR(64)  DEFAULT NULL          COMMENT '开场风格',
  closing_style     VARCHAR(64)  DEFAULT NULL          COMMENT '收尾风格',
  dialogue_ratio    DECIMAL(5,2) DEFAULT NULL          COMMENT '对话占比',
  has_memorable     TINYINT      DEFAULT 0             COMMENT '是否有记忆点',
  consistency_fatal INT          DEFAULT 0              COMMENT '一致性致命错误数',
  consistency_warn  INT          DEFAULT 0              COMMENT '一致性警告数',
  reader_want_next  TINYINT      DEFAULT 1             COMMENT '读者想看下一章',
  raw_meta_json     TEXT         DEFAULT NULL          COMMENT '原始 meta.json 备份',
  moliu_synced_at   DATETIME    DEFAULT NULL           COMMENT '墨流同步时间',
  created_at        DATETIME    DEFAULT CURRENT_TIMESTAMP,
  updated_at        DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_novel_chapter (novel_id, chapter_num),
  INDEX idx_novel_num (novel_id, chapter_num)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小说章节（来自墨流）';

-- ==================== 9. 角色状态历史表（每章一快照） ====================
CREATE TABLE novel_character_state_history (
  id              VARCHAR(32)  NOT NULL               COMMENT '主键，UUID',
  novel_id        VARCHAR(32)  NOT NULL                 COMMENT '所属小说',
  character_id    VARCHAR(32)  NOT NULL                 COMMENT '角色ID',
  chapter_num     INT          NOT NULL                  COMMENT '章节号（这一章后的状态）',
  status          VARCHAR(16)  DEFAULT NULL             COMMENT '状态：active/injured/missing/dead/left',
  location        VARCHAR(128) DEFAULT NULL            COMMENT '位置',
  current_goal    VARCHAR(256) DEFAULT NULL             COMMENT '当前目标',
  current_emotion VARCHAR(64)  DEFAULT NULL             COMMENT '当前情绪',
  physical_state  VARCHAR(128) DEFAULT NULL             COMMENT '身体状态',
  resources       JSON         DEFAULT NULL             COMMENT '持有资源',
  known_info      JSON         DEFAULT NULL             COMMENT '已知信息',
  snapshot_yaml   TEXT         DEFAULT NULL              COMMENT '原始角色卡 YAML 快照',
  synced_at       DATETIME    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_char_chapter (character_id, chapter_num),
  INDEX idx_novel_chapter (novel_id, chapter_num)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色状态历史（每章一快照）';

-- ==================== 10. 伏笔表（墨流同步） ====================
CREATE TABLE novel_foreshadow (
  id               VARCHAR(32)  NOT NULL               COMMENT '主键，UUID',
  novel_id         VARCHAR(32)  NOT NULL               COMMENT '所属小说',
  moliu_id         VARCHAR(32)  DEFAULT NULL            COMMENT '墨流伏笔ID (f001 等)',
  description      VARCHAR(512) NOT NULL                COMMENT '伏笔描述',
  status           VARCHAR(16)  DEFAULT 'planted'       COMMENT '状态：planted/building/paid/dropped',
  priority         VARCHAR(16)  DEFAULT 'normal'        COMMENT '优先级：high/normal/low',
  type             VARCHAR(8)   DEFAULT '明'             COMMENT '类型：明/暗/潜',
  planted_chapter  INT          DEFAULT 0                COMMENT '埋入章节',
  last_advanced    INT          DEFAULT 0                COMMENT '最近推进章节',
  paid_chapter     INT          DEFAULT NULL             COMMENT '回收章节',
  related_characters JSON       DEFAULT NULL             COMMENT '关联角色ID列表',
  moliu_synced_at  DATETIME    DEFAULT NULL              COMMENT '墨流同步时间',
  created_at       DATETIME    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_novel_status (novel_id, status),
  INDEX idx_moliu_id (moliu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小说伏笔（来自墨流）';

-- ==================== 11. 关系抽取任务表 ====================
CREATE TABLE novel_relation_extract_task (
  id            VARCHAR(32) NOT NULL                    COMMENT '主键，UUID',
  novel_id      VARCHAR(32) NOT NULL                    COMMENT '所属小说',
  chapter_num   INT         DEFAULT NULL                COMMENT '章节号（NULL 表示全本）',
  mode          VARCHAR(16) DEFAULT 'incremental'       COMMENT 'incremental/full',
  status        VARCHAR(16) DEFAULT 'pending'          COMMENT 'pending/processing/done/failed',
  extracted_at  DATETIME    DEFAULT NULL               COMMENT '完成时间',
  relations_found INT       DEFAULT 0                   COMMENT '抽取到的关系数',
  error_msg     VARCHAR(512) DEFAULT NULL,
  created_at    DATETIME    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  INDEX idx_novel_status (novel_id, status),
  INDEX idx_novel_chapter (novel_id, chapter_num)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关系抽取任务';
