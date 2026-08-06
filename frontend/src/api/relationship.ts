import { request } from './request'
import type {
  RelationshipSaveDTO,
  RelationshipQueryDTO,
  RelationshipVO,
  RelTypeConfigDTO,
  RelTypeConfigVO,
  PageVO
} from '@/types'

/** 分页查询关系列表 */
export function listRelationships(novelId: string, params: RelationshipQueryDTO) {
  return request.get<PageVO<RelationshipVO>>(`/novel/${novelId}/relationships`, params)
}

/** 创建关系 */
export function createRelationship(novelId: string, data: RelationshipSaveDTO) {
  return request.post<RelationshipVO>(`/novel/${novelId}/relationships`, data)
}

/** 更新关系 */
export function updateRelationship(novelId: string, id: string, data: RelationshipSaveDTO) {
  return request.put<RelationshipVO>(`/novel/${novelId}/relationships/${id}`, data)
}

/** 删除关系 */
export function deleteRelationship(novelId: string, id: string) {
  return request.delete<void>(`/novel/${novelId}/relationships/${id}`)
}

/** 查询小说的关系类型列表 */
export function listRelTypes(novelId: string) {
  return request.get<RelTypeConfigVO[]>(`/novel/${novelId}/rel-types`)
}

/** 新增自定义关系类型 */
export function createRelType(novelId: string, data: RelTypeConfigDTO) {
  return request.post<RelTypeConfigVO>(`/novel/${novelId}/rel-types`, data)
}
