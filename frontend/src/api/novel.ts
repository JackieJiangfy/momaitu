import { request } from './request'
import type { NovelSaveDTO, NovelQueryDTO, NovelVO, PageVO } from '@/types'

/** 分页查询小说列表 */
export function listNovels(params: NovelQueryDTO) {
  return request.get<PageVO<NovelVO>>('/novels', params)
}

/** 获取小说详情 */
export function getNovel(id: string) {
  return request.get<NovelVO>(`/novels/${id}`)
}

/** 创建小说 */
export function createNovel(data: NovelSaveDTO) {
  return request.post<NovelVO>('/novels', data)
}

/** 更新小说 */
export function updateNovel(id: string, data: NovelSaveDTO) {
  return request.put<NovelVO>(`/novels/${id}`, data)
}

/** 删除小说（逻辑删除） */
export function deleteNovel(id: string) {
  return request.delete<void>(`/novels/${id}`)
}
