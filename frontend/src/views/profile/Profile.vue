<template>
  <div class="profile">
    <a-card class="profile__card" :bordered="false">
      <template #title>
        <span class="profile__title">个人中心</span>
      </template>

      <!-- 基本信息展示 -->
      <a-descriptions :column="1" bordered size="large" class="profile__desc">
        <a-descriptions-item label="用户名">
          <span class="mono">{{ userInfo?.username }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="昵称">{{ userInfo?.nickname }}</a-descriptions-item>
        <a-descriptions-item label="账号状态">
          <a-tag v-if="userInfo?.status === 'ACTIVE'" color="green" size="small">正常</a-tag>
          <a-tag v-else color="red" size="small">{{ userInfo?.status }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="注册时间">
          {{ userInfo?.createdAt ? formatTime(userInfo.createdAt) : '-' }}
        </a-descriptions-item>
      </a-descriptions>

      <a-divider />

      <!-- 修改昵称 -->
      <h3 class="profile__section-title">修改昵称</h3>
      <a-form :model="profileForm" layout="inline" @submit-success="handleUpdateProfile">
        <a-form-item field="nickname" :rules="[{ required: true, message: '请输入昵称' }]">
          <a-input v-model="profileForm.nickname" placeholder="新昵称" allow-clear style="width: 240px" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="profileLoading">保存</a-button>
        </a-form-item>
      </a-form>

      <a-divider />

      <!-- 修改密码 -->
      <h3 class="profile__section-title">修改密码</h3>
      <a-form
        :model="passwordForm"
        layout="vertical"
        style="max-width: 360px"
        @submit-success="handleChangePassword"
      >
        <a-form-item field="oldPassword" label="当前密码" :rules="[{ required: true, message: '请输入当前密码' }]">
          <a-input-password v-model="passwordForm.oldPassword" placeholder="请输入当前密码" allow-clear />
        </a-form-item>
        <a-form-item field="newPassword" label="新密码" :rules="newPasswordRules">
          <a-input-password v-model="passwordForm.newPassword" placeholder="6-32 位新密码" allow-clear />
        </a-form-item>
        <a-form-item
          field="confirmPassword"
          label="确认新密码"
          :rules="[{ required: true, message: '请再次输入新密码' }, { validator: validateConfirm }]"
        >
          <a-input-password v-model="passwordForm.confirmPassword" placeholder="再次输入新密码" allow-clear />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="passwordLoading">修改密码</a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, computed } from 'vue'
import { Message, type FieldRule } from '@arco-design/web-vue'
import { useUserStore } from '@/stores/user'
import { updateProfile, changePassword } from '@/api/auth'
import type { UpdateProfileDTO, ChangePasswordDTO } from '@/types'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

// ---- 修改昵称 ----
const profileLoading = ref(false)
const profileForm = reactive<UpdateProfileDTO>({ nickname: '' })

async function handleUpdateProfile() {
  profileLoading.value = true
  try {
    await updateProfile(profileForm)
    await userStore.fetchUserInfo()
    Message.success('昵称已更新')
  } finally {
    profileLoading.value = false
  }
}

// ---- 修改密码 ----
const passwordLoading = ref(false)
const passwordForm = reactive<ChangePasswordDTO & { confirmPassword: string }>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const newPasswordRules: FieldRule[] = [
  { required: true, message: '请输入新密码' },
  { minLength: 6, maxLength: 32, message: '密码长度 6-32 位' }
]
function validateConfirm(value: string | undefined, callback: (error?: string) => void) {
  if (value !== passwordForm.newPassword) {
    callback('两次输入的密码不一致')
  } else {
    callback()
  }
}

async function handleChangePassword() {
  passwordLoading.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    Message.success('密码已修改，请重新登录')
    await userStore.logout()
    location.href = '/login'
  } catch {
    // 错误已由 request.ts 处理
  } finally {
    passwordLoading.value = false
  }
}

// ---- 工具 ----
function formatTime(iso: string): string {
  return new Date(iso).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit'
  })
}

onMounted(async () => {
  // 初始化昵称为当前用户昵称
  if (!userStore.userInfo) {
    await userStore.fetchUserInfo()
  }
  profileForm.nickname = userStore.userInfo?.nickname || ''
})
</script>

<style scoped lang="scss">
.profile {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;

  &__card {
    border-radius: $radius-lg;
    box-shadow: $shadow-card;
  }

  &__title {
    font-size: 18px;
    font-weight: 600;
  }

  &__section-title {
    margin: 0 0 16px;
    font-size: 15px;
    font-weight: 500;
    color: $color-text-primary;
  }

  &__desc {
    :deep(.arco-descriptions-item-label) {
      width: 120px;
      color: $color-text-secondary;
      background: $color-bg-page;
    }
  }
}
</style>
