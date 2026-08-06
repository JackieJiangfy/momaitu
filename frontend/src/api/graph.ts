import { request } from './request'
import type { GraphDataVO } from '@/types'

/** 获取小说的角色关系图谱数据 */
export function getGraph(novelId: string) {
  return request.get<GraphDataVO>(`/novel/${novelId}/graph`)
}
