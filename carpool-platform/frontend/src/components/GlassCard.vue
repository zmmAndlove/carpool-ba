<template>
  <div :class="classes">
    <div v-if="$slots.header" class="glass-card__header">
      <slot name="header" />
    </div>
    <div class="glass-card__body">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    strong?: boolean
    hover?: boolean
    noPadding?: boolean
  }>(),
  {
    strong: false,
    hover: true,
    noPadding: false
  }
)

const classes = computed(() => [
  'glass',
  props.strong && 'glass-strong',
  props.hover && 'glass-hover',
  'glass-card',
  props.noPadding && 'glass-card--noPadding'
])
</script>

<style scoped>
.glass-card {
  overflow: hidden;
}

.glass-card__header {
  padding: 16px 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
}

.glass-card__body {
  padding: 18px;
}

.glass-card--noPadding .glass-card__body {
  padding: 0;
}
</style>
