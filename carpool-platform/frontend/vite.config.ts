import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  // 性能优化配置
  build: {
    // 启用gzip压缩
    cssCodeSplit: true,
    // 代码分割
    rollupOptions: {
      output: {
        manualChunks: {
          // 第三方库单独打包
          vendor: ['vue', 'vue-router', 'pinia'],
          // Element Plus单独打包
          element: ['element-plus', '@element-plus/icons-vue'],
          // axios单独打包
          axios: ['axios'],
          // dayjs单独打包
          dayjs: ['dayjs']
        }
      }
    },
    // 优化图片处理
    assetsInlineLimit: 4096, // 小于4kb的资源内联
    // 启用sourcemap
    sourcemap: false,
    // 最小化
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    }
  },
  // 启用预加载
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'element-plus', '@element-plus/icons-vue', 'axios', 'dayjs']
  }
})