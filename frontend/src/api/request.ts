import axios, { type AxiosInstance, type InternalAxiosRequestConfig, type AxiosResponse } from 'axios'
import { Message } from '@arco-design/web-vue'
import type { Result } from '@/types'
import router from '@/router'

// ---- token 持久化 key ----
export const TOKEN_KEY = 'mt_token'

/** 读取本地 token */
export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

/** 保存 token */
export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

/** 清除 token */
export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

// ---- axios 实例 ----
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

// ---- 请求拦截器：注入 Sa-Token ----
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken()
    if (token) {
      // Sa-Token 默认从 Authorization header 读取
      config.headers.Authorization = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// ---- 响应拦截器：统一处理 ----
service.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    const res = response.data

    // 业务成功
    if (res.code === 200) {
      return res.data as unknown as AxiosResponse
    }

    // 业务失败（如参数校验等）
    Message.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || 'Error'))
  },
  (error) => {
    const status = error.response?.status
    const resData = error.response?.data

    // 401 未授权：token 失效或未登录
    if (status === 401) {
      clearToken()
      Message.error('登录已过期，请重新登录')
      // 避免在登录页重复跳转
      if (router.currentRoute.value.name !== 'Login') {
        router.push({
          name: 'Login',
          query: { redirect: router.currentRoute.value.fullPath }
        })
      }
      return Promise.reject(error)
    }

    // 403 权限不足
    if (status === 403) {
      Message.error('无权访问')
      return Promise.reject(error)
    }

    // 其他错误：后端可能返回 { code, message } 或纯字符串
    const msg = resData?.message || resData?.error || `请求错误 (${status})`
    Message.error(msg)
    return Promise.reject(error)
  }
)

// ---- 封装请求方法（返回业务数据 T，而非 AxiosResponse） ----
export const request = {
  get<T = unknown>(url: string, params?: object) {
    return service.get<T, T>(url, { params })
  },
  post<T = unknown>(url: string, data?: object) {
    return service.post<T, T>(url, data)
  },
  put<T = unknown>(url: string, data?: object) {
    return service.put<T, T>(url, data)
  },
  delete<T = unknown>(url: string, params?: object) {
    return service.delete<T, T>(url, { params })
  }
}

export default service
