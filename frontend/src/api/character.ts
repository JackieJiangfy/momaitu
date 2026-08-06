import { request } from './request'
import type {
  CharacterSaveDTO,
  CharacterQueryDTO,
  CharacterVO,
  BatchCharacterDTO,
  PageVO
} from '@/types'

/** 分页查询角色列表 */
export function listCharacters(novelId: string, params: CharacterQueryDTO) {
  return request.get<PageVO<CharacterVO>>(`/novel/${novelId}/characters`, params)
}

/** 获取角色详情 */
export function getCharacter(novelId: string, id: string) {
  return request.get<CharacterVO>(`/novel/${novelId}/characters/${id}`)
}

/** 创建角色 */
export function createCharacter(novelId: string, data: CharacterSaveDTO) {
  return request.post<CharacterVO>(`/novel/${novelId}/characters`, data)
}

/** 更新角色 */
export function updateCharacter(novelId: string, id: string, data: CharacterSaveDTO) {
  return request.put<CharacterVO>(`/novel/${novelId}/characters/${id}`, data)
}

/** 删除角色 */
export function deleteCharacter(novelId: string, id: string) {
  return request.delete<void>(`/novel/${novelId}/characters/${id}`)
}

/** 批量导入角色 */
export function batchCreateCharacters(novelId: string, data: BatchCharacterDTO) {
  return request.post<CharacterVO[]>(`/novel/${novelId}/characters/batch`, data)
}
