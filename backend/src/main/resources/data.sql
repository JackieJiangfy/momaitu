-- ============================================================
-- 小说角色关系图谱系统 - 预置数据
-- 说明：25 种预设关系类型，作为模板存储。
--      新建小说时由 Service 层从 sys_user 的内置模板复制到 novel_rel_type_config。
--      此处使用一个特殊的 novel_id = 'SYSTEM_PRESET' 作为系统模板标识。
-- ============================================================

USE novel_graph;

-- 清空系统预置模板（避免重复执行报错）
DELETE FROM novel_rel_type_config WHERE novel_id = 'SYSTEM_PRESET';

-- ==================== 25 种预置关系类型 ====================
-- 大类 1：血缘关系（positive）
INSERT INTO novel_rel_type_config (id, novel_id, type_name, category, sort_order) VALUES
  ('preset-001', 'SYSTEM_PRESET', '父子',     'positive', 1),
  ('preset-002', 'SYSTEM_PRESET', '母子',     'positive', 2),
  ('preset-003', 'SYSTEM_PRESET', '兄妹',     'positive', 3),
  ('preset-004', 'SYSTEM_PRESET', '祖孙',     'positive', 4),
  ('preset-005', 'SYSTEM_PRESET', '姻亲',     'positive', 5);

-- 大类 2：情感关系（positive）
INSERT INTO novel_rel_type_config (id, novel_id, type_name, category, sort_order) VALUES
  ('preset-006', 'SYSTEM_PRESET', '恋人',     'positive', 11),
  ('preset-007', 'SYSTEM_PRESET', '夫妻',     'positive', 12),
  ('preset-008', 'SYSTEM_PRESET', '暗恋',     'positive', 13),
  ('preset-009', 'SYSTEM_PRESET', '知己',     'positive', 14),
  ('preset-010', 'SYSTEM_PRESET', '前任',     'positive', 15);

-- 大类 3：社交关系（neutral）
INSERT INTO novel_rel_type_config (id, novel_id, type_name, category, sort_order) VALUES
  ('preset-011', 'SYSTEM_PRESET', '朋友',     'neutral', 21),
  ('preset-012', 'SYSTEM_PRESET', '师徒',     'neutral', 22),
  ('preset-013', 'SYSTEM_PRESET', '同门',     'neutral', 23),
  ('preset-014', 'SYSTEM_PRESET', '盟友',     'neutral', 24),
  ('preset-015', 'SYSTEM_PRESET', '从属',     'neutral', 25);

-- 大类 4：敌对关系（negative）
INSERT INTO novel_rel_type_config (id, novel_id, type_name, category, sort_order) VALUES
  ('preset-016', 'SYSTEM_PRESET', '仇敌',     'negative', 31),
  ('preset-017', 'SYSTEM_PRESET', '宿敌',     'negative', 32),
  ('preset-018', 'SYSTEM_PRESET', '对手',     'negative', 33),
  ('preset-019', 'SYSTEM_PRESET', '叛徒',     'negative', 34),
  ('preset-020', 'SYSTEM_PRESET', '对立',     'negative', 35);

-- 大类 5：特殊关系（neutral）
INSERT INTO novel_rel_type_config (id, novel_id, type_name, category, sort_order) VALUES
  ('preset-021', 'SYSTEM_PRESET', '主仆',     'neutral', 41),
  ('preset-022', 'SYSTEM_PRESET', '转世',     'neutral', 42),
  ('preset-023', 'SYSTEM_PRESET', '契约',     'neutral', 43),
  ('preset-024', 'SYSTEM_PRESET', '宿命',     'neutral', 44),
  ('preset-025', 'SYSTEM_PRESET', '分身',     'neutral', 45);
