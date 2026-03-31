// vite.config.ts
import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "file:///D:/Cursor-Project/carpool-ba/carpool-platform/frontend/node_modules/vite/dist/node/index.js";
import vue from "file:///D:/Cursor-Project/carpool-ba/carpool-platform/frontend/node_modules/@vitejs/plugin-vue/dist/index.mjs";
var __vite_injected_original_import_meta_url = "file:///D:/Cursor-Project/carpool-ba/carpool-platform/frontend/vite.config.ts";
var vite_config_default = defineConfig({
  plugins: [
    vue()
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", __vite_injected_original_import_meta_url))
    }
  },
  server: {
    port: 3e3,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
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
          vendor: ["vue", "vue-router", "pinia"],
          // Element Plus单独打包
          element: ["element-plus", "@element-plus/icons-vue"],
          // axios单独打包
          axios: ["axios"],
          // dayjs单独打包
          dayjs: ["dayjs"]
        }
      }
    },
    // 优化图片处理
    assetsInlineLimit: 4096,
    // 小于4kb的资源内联
    // 启用sourcemap
    sourcemap: false,
    // 最小化
    minify: "terser",
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    }
  },
  // 启用预加载
  optimizeDeps: {
    include: ["vue", "vue-router", "pinia", "element-plus", "@element-plus/icons-vue", "axios", "dayjs"]
  }
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcudHMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJEOlxcXFxDdXJzb3ItUHJvamVjdFxcXFxjYXJwb29sLWJhXFxcXGNhcnBvb2wtcGxhdGZvcm1cXFxcZnJvbnRlbmRcIjtjb25zdCBfX3ZpdGVfaW5qZWN0ZWRfb3JpZ2luYWxfZmlsZW5hbWUgPSBcIkQ6XFxcXEN1cnNvci1Qcm9qZWN0XFxcXGNhcnBvb2wtYmFcXFxcY2FycG9vbC1wbGF0Zm9ybVxcXFxmcm9udGVuZFxcXFx2aXRlLmNvbmZpZy50c1wiO2NvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9pbXBvcnRfbWV0YV91cmwgPSBcImZpbGU6Ly8vRDovQ3Vyc29yLVByb2plY3QvY2FycG9vbC1iYS9jYXJwb29sLXBsYXRmb3JtL2Zyb250ZW5kL3ZpdGUuY29uZmlnLnRzXCI7aW1wb3J0IHsgZmlsZVVSTFRvUGF0aCwgVVJMIH0gZnJvbSAnbm9kZTp1cmwnXHJcbmltcG9ydCB7IGRlZmluZUNvbmZpZyB9IGZyb20gJ3ZpdGUnXHJcbmltcG9ydCB2dWUgZnJvbSAnQHZpdGVqcy9wbHVnaW4tdnVlJ1xyXG5cclxuLy8gaHR0cHM6Ly92aXRlanMuZGV2L2NvbmZpZy9cclxuZXhwb3J0IGRlZmF1bHQgZGVmaW5lQ29uZmlnKHtcclxuICBwbHVnaW5zOiBbXHJcbiAgICB2dWUoKSxcclxuICBdLFxyXG4gIHJlc29sdmU6IHtcclxuICAgIGFsaWFzOiB7XHJcbiAgICAgICdAJzogZmlsZVVSTFRvUGF0aChuZXcgVVJMKCcuL3NyYycsIGltcG9ydC5tZXRhLnVybCkpXHJcbiAgICB9XHJcbiAgfSxcclxuICBzZXJ2ZXI6IHtcclxuICAgIHBvcnQ6IDMwMDAsXHJcbiAgICBwcm94eToge1xyXG4gICAgICAnL2FwaSc6IHtcclxuICAgICAgICB0YXJnZXQ6ICdodHRwOi8vbG9jYWxob3N0OjgwODAnLFxyXG4gICAgICAgIGNoYW5nZU9yaWdpbjogdHJ1ZVxyXG4gICAgICB9XHJcbiAgICB9XHJcbiAgfSxcclxuICAvLyBcdTYwMjdcdTgwRkRcdTRGMThcdTUzMTZcdTkxNERcdTdGNkVcclxuICBidWlsZDoge1xyXG4gICAgLy8gXHU1NDJGXHU3NTI4Z3ppcFx1NTM4Qlx1N0YyOVxyXG4gICAgY3NzQ29kZVNwbGl0OiB0cnVlLFxyXG4gICAgLy8gXHU0RUUzXHU3ODAxXHU1MjA2XHU1MjcyXHJcbiAgICByb2xsdXBPcHRpb25zOiB7XHJcbiAgICAgIG91dHB1dDoge1xyXG4gICAgICAgIG1hbnVhbENodW5rczoge1xyXG4gICAgICAgICAgLy8gXHU3QjJDXHU0RTA5XHU2NUI5XHU1RTkzXHU1MzU1XHU3MkVDXHU2MjUzXHU1MzA1XHJcbiAgICAgICAgICB2ZW5kb3I6IFsndnVlJywgJ3Z1ZS1yb3V0ZXInLCAncGluaWEnXSxcclxuICAgICAgICAgIC8vIEVsZW1lbnQgUGx1c1x1NTM1NVx1NzJFQ1x1NjI1M1x1NTMwNVxyXG4gICAgICAgICAgZWxlbWVudDogWydlbGVtZW50LXBsdXMnLCAnQGVsZW1lbnQtcGx1cy9pY29ucy12dWUnXSxcclxuICAgICAgICAgIC8vIGF4aW9zXHU1MzU1XHU3MkVDXHU2MjUzXHU1MzA1XHJcbiAgICAgICAgICBheGlvczogWydheGlvcyddLFxyXG4gICAgICAgICAgLy8gZGF5anNcdTUzNTVcdTcyRUNcdTYyNTNcdTUzMDVcclxuICAgICAgICAgIGRheWpzOiBbJ2RheWpzJ11cclxuICAgICAgICB9XHJcbiAgICAgIH1cclxuICAgIH0sXHJcbiAgICAvLyBcdTRGMThcdTUzMTZcdTU2RkVcdTcyNDdcdTU5MDRcdTc0MDZcclxuICAgIGFzc2V0c0lubGluZUxpbWl0OiA0MDk2LCAvLyBcdTVDMEZcdTRFOEU0a2JcdTc2ODRcdThENDRcdTZFOTBcdTUxODVcdTgwNTRcclxuICAgIC8vIFx1NTQyRlx1NzUyOHNvdXJjZW1hcFxyXG4gICAgc291cmNlbWFwOiBmYWxzZSxcclxuICAgIC8vIFx1NjcwMFx1NUMwRlx1NTMxNlxyXG4gICAgbWluaWZ5OiAndGVyc2VyJyxcclxuICAgIHRlcnNlck9wdGlvbnM6IHtcclxuICAgICAgY29tcHJlc3M6IHtcclxuICAgICAgICBkcm9wX2NvbnNvbGU6IHRydWUsXHJcbiAgICAgICAgZHJvcF9kZWJ1Z2dlcjogdHJ1ZVxyXG4gICAgICB9XHJcbiAgICB9XHJcbiAgfSxcclxuICAvLyBcdTU0MkZcdTc1MjhcdTk4ODRcdTUyQTBcdThGN0RcclxuICBvcHRpbWl6ZURlcHM6IHtcclxuICAgIGluY2x1ZGU6IFsndnVlJywgJ3Z1ZS1yb3V0ZXInLCAncGluaWEnLCAnZWxlbWVudC1wbHVzJywgJ0BlbGVtZW50LXBsdXMvaWNvbnMtdnVlJywgJ2F4aW9zJywgJ2RheWpzJ11cclxuICB9XHJcbn0pIl0sCiAgIm1hcHBpbmdzIjogIjtBQUE4VixTQUFTLGVBQWUsV0FBVztBQUNqWSxTQUFTLG9CQUFvQjtBQUM3QixPQUFPLFNBQVM7QUFGNk0sSUFBTSwyQ0FBMkM7QUFLOVEsSUFBTyxzQkFBUSxhQUFhO0FBQUEsRUFDMUIsU0FBUztBQUFBLElBQ1AsSUFBSTtBQUFBLEVBQ047QUFBQSxFQUNBLFNBQVM7QUFBQSxJQUNQLE9BQU87QUFBQSxNQUNMLEtBQUssY0FBYyxJQUFJLElBQUksU0FBUyx3Q0FBZSxDQUFDO0FBQUEsSUFDdEQ7QUFBQSxFQUNGO0FBQUEsRUFDQSxRQUFRO0FBQUEsSUFDTixNQUFNO0FBQUEsSUFDTixPQUFPO0FBQUEsTUFDTCxRQUFRO0FBQUEsUUFDTixRQUFRO0FBQUEsUUFDUixjQUFjO0FBQUEsTUFDaEI7QUFBQSxJQUNGO0FBQUEsRUFDRjtBQUFBO0FBQUEsRUFFQSxPQUFPO0FBQUE7QUFBQSxJQUVMLGNBQWM7QUFBQTtBQUFBLElBRWQsZUFBZTtBQUFBLE1BQ2IsUUFBUTtBQUFBLFFBQ04sY0FBYztBQUFBO0FBQUEsVUFFWixRQUFRLENBQUMsT0FBTyxjQUFjLE9BQU87QUFBQTtBQUFBLFVBRXJDLFNBQVMsQ0FBQyxnQkFBZ0IseUJBQXlCO0FBQUE7QUFBQSxVQUVuRCxPQUFPLENBQUMsT0FBTztBQUFBO0FBQUEsVUFFZixPQUFPLENBQUMsT0FBTztBQUFBLFFBQ2pCO0FBQUEsTUFDRjtBQUFBLElBQ0Y7QUFBQTtBQUFBLElBRUEsbUJBQW1CO0FBQUE7QUFBQTtBQUFBLElBRW5CLFdBQVc7QUFBQTtBQUFBLElBRVgsUUFBUTtBQUFBLElBQ1IsZUFBZTtBQUFBLE1BQ2IsVUFBVTtBQUFBLFFBQ1IsY0FBYztBQUFBLFFBQ2QsZUFBZTtBQUFBLE1BQ2pCO0FBQUEsSUFDRjtBQUFBLEVBQ0Y7QUFBQTtBQUFBLEVBRUEsY0FBYztBQUFBLElBQ1osU0FBUyxDQUFDLE9BQU8sY0FBYyxTQUFTLGdCQUFnQiwyQkFBMkIsU0FBUyxPQUFPO0FBQUEsRUFDckc7QUFDRixDQUFDOyIsCiAgIm5hbWVzIjogW10KfQo=
