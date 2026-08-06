<template>
  <div class="auth">
    <!-- 左侧品牌展示区 -->
    <aside class="auth__brand">
      <div class="auth__brand-inner">
        <div class="auth__logo">
          <span class="auth__logo-mark">墨</span>
          <span class="auth__logo-text">脉图</span>
        </div>
        <h1 class="auth__title">小说角色<br />关系图谱系统</h1>
        <p class="auth__desc">梳理角色脉络 · 构建故事世界</p>
        <div class="auth__decor">
          <span class="auth__decor-line"></span>
          <span class="auth__decor-text mono">v1.0</span>
        </div>
      </div>
    </aside>

    <!-- 右侧表单区 -->
    <main class="auth__form">
      <div class="auth__form-inner">
        <a-tabs v-model:active-key="activeTab" class="auth__tabs">
          <a-tab-pane key="login" title="登录">
            <a-form
              :model="loginForm"
              :rules="loginRules"
              layout="vertical"
              @submit-success="handleLogin"
            >
              <a-form-item field="username" label="用户名">
                <a-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  allow-clear
                  size="large"
                />
              </a-form-item>
              <a-form-item field="password" label="密码">
                <a-input-password
                  v-model="loginForm.password"
                  placeholder="请输入密码"
                  allow-clear
                  size="large"
                />
              </a-form-item>
              <a-form-item>
                <a-button
                  type="primary"
                  html-type="submit"
                  long
                  size="large"
                  :loading="loginLoading"
                >
                  登录
                </a-button>
              </a-form-item>
            </a-form>
          </a-tab-pane>

          <a-tab-pane key="register" title="注册">
            <a-form
              :model="registerForm"
              :rules="registerRules"
              layout="vertical"
              @submit-success="handleRegister"
            >
              <a-form-item field="username" label="用户名">
                <a-input
                  v-model="registerForm.username"
                  placeholder="3-32 位字符"
                  allow-clear
                  size="large"
                />
              </a-form-item>
              <a-form-item field="nickname" label="昵称">
                <a-input
                  v-model="registerForm.nickname"
                  placeholder="请输入昵称"
                  allow-clear
                  size="large"
                />
              </a-form-item>
              <a-form-item field="password" label="密码">
                <a-input-password
                  v-model="registerForm.password"
                  placeholder="至少 6 位"
                  allow-clear
                  size="large"
                />
              </a-form-item>
              <a-form-item>
                <a-button
                  type="primary"
                  html-type="submit"
                  long
                  size="large"
                  :loading="registerLoading"
                >
                  注册
                </a-button>
              </a-form-item>
            </a-form>
          </a-tab-pane>
        </a-tabs>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Message, type FieldRule } from '@arco-design/web-vue'
import { useUserStore } from '@/stores/user'
import type { LoginDTO, RegisterDTO } from '@/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeTab = ref<'login' | 'register'>('login')

// ---- 登录表单 ----
const loginLoading = ref(false)
const loginForm = reactive<LoginDTO>({
  username: '',
  password: ''
})
const loginRules: Record<string, FieldRule[]> = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}

async function handleLogin() {
  loginLoading.value = true
  try {
    await userStore.login(loginForm)
    Message.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    router.replace(redirect)
  } catch {
    // 错误已由 request.ts 拦截器处理
  } finally {
    loginLoading.value = false
  }
}

// ---- 注册表单 ----
const registerLoading = ref(false)
const registerForm = reactive<RegisterDTO>({
  username: '',
  password: '',
  nickname: ''
})
const registerRules: Record<string, FieldRule[]> = {
  username: [
    { required: true, message: '请输入用户名' },
    { minLength: 3, maxLength: 32, message: '用户名长度 3-32 位' }
  ],
  nickname: [
    { required: true, message: '请输入昵称' },
    { maxLength: 32, message: '昵称长度不能超过 32 位' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { minLength: 6, maxLength: 32, message: '密码长度 6-32 位' }
  ]
}

async function handleRegister() {
  registerLoading.value = true
  try {
    await userStore.register(registerForm)
    Message.success('注册成功，请登录')
    // 切换到登录 tab，并预填用户名
    loginForm.username = registerForm.username
    loginForm.password = ''
    activeTab.value = 'login'
  } catch {
    // 错误已由 request.ts 拦截器处理
  } finally {
    registerLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.auth {
  display: flex;
  height: 100vh;
  overflow: hidden;

  // ---- 左侧品牌区 ----
  &__brand {
    flex: 0 0 42%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #0f3d6e 0%, $color-primary 60%, #1a6bb8 100%);
    color: #fff;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background-image:
        radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.06) 0%, transparent 50%),
        radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.04) 0%, transparent 40%);
    }
  }

  &__brand-inner {
    position: relative;
    padding: 48px;
    max-width: 420px;
  }

  &__logo {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 64px;
  }

  &__logo-mark {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 44px;
    height: 44px;
    border-radius: 10px;
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(8px);
    font-size: 24px;
    font-weight: 700;
  }

  &__logo-text {
    font-size: 22px;
    font-weight: 600;
    letter-spacing: 2px;
  }

  &__title {
    margin: 0 0 16px;
    font-size: 36px;
    font-weight: 700;
    line-height: 1.3;
    letter-spacing: 1px;
  }

  &__desc {
    margin: 0 0 48px;
    font-size: 15px;
    opacity: 0.75;
    letter-spacing: 2px;
  }

  &__decor {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__decor-line {
    flex: 1;
    height: 1px;
    background: rgba(255, 255, 255, 0.2);
  }

  &__decor-text {
    font-size: 12px;
    opacity: 0.5;
  }

  // ---- 右侧表单区 ----
  &__form {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    background: $color-bg-card;
  }

  &__form-inner {
    width: 100%;
    max-width: 380px;
    padding: 48px 32px;
  }

  &__tabs {
    :deep(.arco-tabs-nav) {
      margin-bottom: 32px;
    }
    :deep(.arco-tabs-tab) {
      font-size: 16px;
      font-weight: 500;
    }
  }
}

// ---- 响应式：窄屏隐藏品牌区 ----
@media (max-width: 768px) {
  .auth__brand {
    display: none;
  }
  .auth__form-inner {
    padding: 24px;
  }
}
</style>
