// ============================================
// 墨脉图 - TypeScript 类型定义
// 与后端 com.novelgraph.dto 包下的 VO/DTO 对齐
// ============================================

// ---- 通用响应 ----

/** 后端统一响应体 Result<T> */
export interface Result<T = unknown> {
  code: number
  message: string
  data: T
}

/** 分页响应（MyBatis-Plus IPage 序列化格式） */
export interface PageVO<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// ---- 认证相关 ----

export interface LoginDTO {
  username: string
  password: string
}

export interface RegisterDTO {
  username: string
  password: string
  nickname: string
}

export interface UpdateProfileDTO {
  nickname?: string
  avatar?: string
}

export interface ChangePasswordDTO {
  oldPassword: string
  newPassword: string
}

/** 登录响应 */
export interface LoginVO {
  tokenName: string
  token: string
}

/** 用户信息 */
export interface UserInfoVO {
  id: string
  username: string
  nickname: string
  avatar: string
  status: string
  createdAt: string
}

// ---- 小说相关 ----

export interface NovelSaveDTO {
  title: string
  author?: string
  description?: string
  coverUrl?: string
}

export interface NovelQueryDTO {
  keyword?: string
  page?: number
  size?: number
}

export interface NovelVO {
  id: string
  userId: string
  title: string
  author: string
  description: string
  coverUrl: string
  status: string
  characterCount: number
  relationCount: number
  createdAt: string
  updatedAt: string
}

// ---- 角色相关 ----

export interface CharacterSaveDTO {
  name: string
  alias?: string
  faction?: string
  roleType?: string
  species?: string
  avatarUrl?: string
  description?: string
  firstChapter?: string
  powerLevel?: string
  sortOrder?: number
}

export interface BatchCharacterDTO {
  characters: CharacterSaveDTO[]
}

export interface CharacterQueryDTO {
  keyword?: string
  faction?: string
  roleType?: string
  page?: number
  size?: number
}

export interface CharacterVO {
  id: string
  novelId: string
  name: string
  alias: string
  faction: string
  roleType: string
  species: string
  avatarUrl: string
  description: string
  firstChapter: string
  powerLevel: string
  sortOrder: number
  /** 关联关系数（用于图谱节点大小计算） */
  relationCount: number
  createdAt: string
  updatedAt: string
}

// ---- 关系相关 ----

export interface RelationshipSaveDTO {
  sourceId: string
  targetId: string
  relType: string
  category: string
  directed?: number
  intensity?: number
  description?: string
  startChapter?: string
  endChapter?: string
}

export interface RelationshipQueryDTO {
  keyword?: string
  category?: string
  characterId?: string
  page?: number
  size?: number
}

export interface RelationshipVO {
  id: string
  novelId: string
  sourceId: string
  targetId: string
  /** 源角色名（冗余字段） */
  sourceName: string
  /** 目标角色名（冗余字段） */
  targetName: string
  relType: string
  category: string
  directed: number
  intensity: number
  description: string
  startChapter: string
  endChapter: string
  createdAt: string
  updatedAt: string
}

// ---- 关系类型配置 ----

export interface RelTypeConfigDTO {
  typeName: string
  category: string
  icon?: string
  sortOrder?: number
}

export interface RelTypeConfigVO {
  id: string
  novelId: string
  typeName: string
  category: string
  icon: string
  sortOrder: number
  createdAt: string
}

// ---- 图谱数据 ----

export interface GraphNodeVO {
  id: string
  name: string
  alias: string
  faction: string
  roleType: string
  species: string
  avatarUrl: string
  /** 关联关系数（用于计算节点大小） */
  relationCount: number
}

export interface GraphEdgeVO {
  id: string
  source: string
  target: string
  relType: string
  category: string
  directed: boolean
  intensity: number
}

export interface GraphDataVO {
  novelTitle: string
  nodes: GraphNodeVO[]
  edges: GraphEdgeVO[]
}
