<template>
  <div class="not-found">
    <div class="not-found__code mono">404</div>
    <p class="not-found__text">页面不存在</p>
    <a-button type="primary" @click="$router.replace('/')">返回首页</a-button>
  </div>
</template>

<script setup lang="ts">
// 404 页面
</script>

<style scoped lang="scss">
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  gap: 16px;

  &__code {
    font-size: 96px;
    font-weight: 700;
    color: $color-primary;
    letter-spacing: 4px;
  }

  &__text {
    margin: 0 0 16px;
    color: $color-text-secondary;
    font-size: 16px;
  }
}
</style>
