import { request } from './request'
import type {
  LoginDTO,
  LoginVO,
  RegisterDTO,
  UpdateProfileDTO,
  ChangePasswordDTO,
  UserInfoVO
} from '@/types'

/** 注册 */
export function register(data: RegisterDTO) {
  return request.post<UserInfoVO>('/auth/register', data)
}

/** 登录 */
export function login(data: LoginDTO) {
  return request.post<LoginVO>('/auth/login', data)
}

/** 退出登录 */
export function logout() {
  return request.post<void>('/auth/logout')
}

/** 获取当前用户信息 */
export function getUserInfo() {
  return request.get<UserInfoVO>('/auth/info')
}

/** 更新个人资料 */
export function updateProfile(data: UpdateProfileDTO) {
  return request.put<UserInfoVO>('/auth/profile', data)
}

/** 修改密码 */
export function changePassword(data: ChangePasswordDTO) {
  return request.put<void>('/auth/password', data)
}
