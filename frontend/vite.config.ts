import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '127.0.0.1',
    port: 5173,
    proxy: {
      // 将 /api 请求代理到后端 Spring Boot 服务
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      }
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        // 全局注入变量与混入（无需在每个文件手动 import）
        additionalData: `@use "@/assets/styles/variables.scss" as *;`
      }
    }
  },
  build: {
    // 分包策略：将大依赖拆为独立 chunk，减小首屏体积
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes('node_modules')) return undefined
          // Vue 运行时核心
          if (/[\\/]node_modules[\\/](vue|@vue|vue-router|pinia)[\\/]/.test(id)) {
            return 'vue-vendor'
          }
          // Arco Design 组件库（~400KB）
          if (id.includes('@arco-design')) {
            return 'arco-design'
          }
          // ECharts 图表库（~400KB，仅图谱页使用）
          if (/[\\/]node_modules[\\/](echarts|zrender)[\\/]/.test(id)) {
            return 'echarts'
          }
          return undefined
        }
      }
    },
    // 提升警告阈值，避免常规分包被误报
    chunkSizeWarningLimit: 800
  }
})
