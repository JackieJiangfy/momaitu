<template>
  <a-layout class="layout">
    <!-- 顶栏 -->
    <a-layout-header class="layout__header">
      <div class="layout__header-inner">
        <!-- 左侧 Logo -->
        <div class="layout__logo" @click="router.push('/')">
          <span class="layout__logo-mark">墨</span>
          <span class="layout__logo-text">脉图</span>
        </div>

        <!-- 中间导航 -->
        <a-menu
          mode="horizontal"
          :selected-keys="[activeMenu]"
          class="layout__menu"
        >
          <a-menu-item key="novels" @click="router.push('/novels')">
            <template #icon><icon-book /></template>
            我的小说
          </a-menu-item>
        </a-menu>

        <!-- 右侧用户区 -->
        <div class="layout__user">
          <a-dropdown trigger="hover">
            <div class="layout__user-trigger">
              <a-avatar :size="32" :style="{ backgroundColor: '#16599e' }">
                {{ userStore.nickname.charAt(0).toUpperCase() }}
              </a-avatar>
              <span class="layout__user-name">{{ userStore.nickname }}</span>
            </div>
            <template #content>
              <a-doption @click="router.push('/profile')">
                <template #icon><icon-user /></template>
                个人中心
              </a-doption>
              <a-doption @click="handleLogout">
                <template #icon><icon-export /></template>
                退出登录
              </a-doption>
            </template>
          </a-dropdown>
        </div>
      </div>
    </a-layout-header>

    <!-- 内容区 -->
    <a-layout-content class="layout__content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </a-layout-content>
  </a-layout>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Message } from '@arco-design/web-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 当前激活的菜单项（基于路由前缀匹配）
const activeMenu = computed(() => {
  if (route.path.startsWith('/novels')) return 'novels'
  if (route.path.startsWith('/novel/')) return 'novels'
  return ''
})

async function handleLogout() {
  await userStore.logout()
  Message.success('已退出登录')
  router.replace('/login')
}
</script>

<style scoped lang="scss">
.layout {
  height: 100vh;

  &__header {
    height: 56px;
    background: $color-bg-card;
    border-bottom: 1px solid $color-border;
    padding: 0;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.03);
    position: relative;
    z-index: 10;
  }

  &__header-inner {
    height: 100%;
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 24px;
    display: flex;
    align-items: center;
    gap: 32px;
  }

  &__logo {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    user-select: none;
  }

  &__logo-mark {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    border-radius: 6px;
    background: $color-primary;
    color: #fff;
    font-size: 16px;
    font-weight: 700;
  }

  &__logo-text {
    font-size: 16px;
    font-weight: 600;
    color: $color-text-primary;
    letter-spacing: 1px;
  }

  &__menu {
    flex: 1;
    background: transparent;
    border-bottom: none;
  }

  &__user {
    margin-left: auto;
  }

  &__user-trigger {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: $radius-md;
    transition: background 0.2s;

    &:hover {
      background: $color-bg-page;
    }
  }

  &__user-name {
    font-size: 14px;
    color: $color-text-primary;
  }

  &__content {
    background: $color-bg-page;
    overflow-y: auto;
  }
}
</style>
