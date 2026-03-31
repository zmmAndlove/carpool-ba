<template>
  <Layout>
    <div class="page-shell">
      <div v-if="title || $slots.header" class="page-shell__top pixel pixel-strong">
        <div class="page-shell__topLeft">
          <div v-if="title" class="page-shell__title">{{ title }}</div>
          <div v-if="subtitle" class="page-shell__subtitle">{{ subtitle }}</div>
        </div>
        <div v-if="$slots.header" class="page-shell__topRight">
          <slot name="header" />
        </div>
      </div>

      <div class="page-shell__content">
        <slot />
      </div>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import Layout from '@/components/Layout.vue'

defineProps<{
  title?: string
  subtitle?: string
}>()
</script>

<style scoped>
.page-shell {
  width: 100%;
}

.page-shell__top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
  padding: 16px 18px;
  transition: all 0.1s ease;
}

.page-shell__top:hover {
  transform: translate(-1px, -1px);
  box-shadow: var(--pixel-shadow-hover);
}

.page-shell__title {
  font-size: 16px;
  font-weight: bold;
  color: var(--brand);
  text-shadow: 2px 2px 0px rgba(0, 0, 0, 0.2);
}

.page-shell__subtitle {
  margin-top: 8px;
  color: var(--text-2);
  font-size: 12px;
}

.page-shell__topRight {
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-shell__content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>
