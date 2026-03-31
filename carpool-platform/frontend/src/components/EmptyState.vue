<template>
  <div class="empty-state glass glass-hover">
    <el-empty :image-size="imageSize" :description="description">
      <template #default>
        <div class="empty-state__title" v-if="title">{{ title }}</div>
        <div class="empty-state__desc" v-if="description">{{ description }}</div>
        <div class="empty-state__actions" v-if="$slots.actions">
          <slot name="actions" />
        </div>
      </template>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    title?: string
    description?: string
    imageSize?: number
  }>(),
  {
    imageSize: 120
  }
)
</script>

<style scoped>
.empty-state {
  padding: 18px;
}

.empty-state__title {
  font-size: 16px;
  font-weight: 700;
  margin-top: 8px;
}

.empty-state__desc {
  margin-top: 6px;
  color: var(--text-2);
  font-size: 13px;
}

.empty-state__actions {
  margin-top: 14px;
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>
