import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, logout as logoutApi, getUserInfo } from '@/api/auth'
import { getToken, setToken, clearToken } from '@/api/request'
import type { LoginDTO, RegisterDTO, UserInfoVO } from '@/types'

export const useUserStore = defineStore('user', () => {
  // ---- state ----
  const token = ref<string | null>(getToken())
  const userInfo = ref<UserInfoVO | null>(null)

  // ---- getters ----
  const isLoggedIn = computed(() => !!token.value)
  const nickname = computed(() => userInfo.value?.nickname || userInfo.value?.username || '')

  // ---- actions ----

  /** 登录：保存 token 并拉取用户信息 */
  async function login(dto: LoginDTO) {
    const res = await loginApi(dto)
    token.value = res.token
    setToken(res.token)
    await fetchUserInfo()
  }

  /** 注册 */
  async function register(dto: RegisterDTO) {
    return registerApi(dto)
  }

  /** 拉取当前用户信息 */
  async function fetchUserInfo() {
    const info = await getUserInfo()
    userInfo.value = info
    return info
  }

  /** 退出：清状态 + 调后端登出 */
  async function logout() {
    try {
      await logoutApi()
    } finally {
      // 即使后端登出失败，前端也清除本地状态
      resetState()
    }
  }

  /** 重置本地状态（token + userInfo） */
  function resetState() {
    token.value = null
    userInfo.value = null
    clearToken()
  }

  return {
    // state
    token,
    userInfo,
    // getters
    isLoggedIn,
    nickname,
    // actions
    login,
    register,
    fetchUserInfo,
    logout,
    resetState
  }
})
