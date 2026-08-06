<template>
  <div class="home">
    <div class="home__card">
      <h1 class="home__title">墨脉图</h1>
      <p class="home__subtitle">小说角色关系图谱系统</p>
      <a-divider />
      <p class="home__status">
        <a-badge status="success" text="前端骨架已就绪" />
      </p>
      <p class="home__hint text-secondary">技术栈：Vue 3 + Vite + TypeScript + Arco Design</p>
    </div>
  </div>
</template>

<script setup lang="ts">
// 占位首页：后续步骤将替换为登录/主布局
</script>

<style scoped lang="scss">
.home {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: $color-bg-page;

  &__card {
    text-align: center;
    padding: 48px 64px;
    background: $color-bg-card;
    border-radius: $radius-lg;
    box-shadow: $shadow-card;
  }

  &__title {
    margin: 0;
    font-size: 42px;
    font-weight: 700;
    color: $color-primary;
    letter-spacing: 4px;
  }

  &__subtitle {
    margin: 8px 0 24px;
    color: $color-text-secondary;
    font-size: 14px;
  }

  &__status {
    margin: 16px 0;
  }

  &__hint {
    margin: 0;
    font-size: 12px;
  }
}
</style>
