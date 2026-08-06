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
  }
})
