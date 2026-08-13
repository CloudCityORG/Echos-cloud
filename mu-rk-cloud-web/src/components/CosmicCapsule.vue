<script setup lang="ts">
import { computed, ref } from 'vue'
import { useCosmicRenderer } from '@/composables/useCosmicRenderer'
import { getPresetByIndex, type CapsulePreset } from '@/utils/presets'

interface RankInfo {
  rankId: number
  rankName: string
  username: string
  voteCount: number
  loveCount: number
}

const props = withDefaults(
  defineProps<{
    index?: number
    preset?: CapsulePreset
    rank?: RankInfo
    size?: 'small' | 'medium' | 'large' | 'hero'
    showInfo?: boolean
    clickable?: boolean
  }>(),
  {
    index: 0,
    size: 'medium',
    showInfo: true,
    clickable: true
  }
)

const emit = defineEmits<{
  click: [rank: Pick<RankInfo, 'rankId' | 'rankName' | 'username' | 'voteCount' | 'loveCount'> | undefined]
}>()

const canvasRef = ref<HTMLCanvasElement | null>(null)
const activePreset = computed(() => props.preset || getPresetByIndex(props.index))

useCosmicRenderer(canvasRef, () => activePreset.value, {
  dprCap: props.size === 'small' ? 1.2 : props.size === 'hero' ? 2 : 1.55
})

const sizeClass = computed(() => `capsule-${props.size}`)
const themeClass = computed(() => activePreset.value.theme || 'light')

const handleClick = () => {
  if (props.clickable) emit('click', props.rank)
}
</script>

<template>
  <button
    type="button"
    class="cosmic-capsule"
    :class="[sizeClass, themeClass, { 'is-clickable': clickable }]"
    @click="handleClick"
  >
    <span class="capsule-visual">
      <canvas ref="canvasRef" aria-hidden="true"></canvas>
    </span>

    <span v-if="showInfo" class="capsule-overlay">
      <span class="capsule-meta">
        <span class="capsule-code">{{ activePreset.code }}</span>
        <span v-if="rank" class="capsule-live">LIVE</span>
      </span>
      <span class="capsule-body">
        <span class="capsule-name">{{ rank?.rankName || activePreset.name }}</span>
        <span v-if="rank" class="capsule-author">@{{ rank.username }}</span>
        <span v-else class="capsule-author">画境观屿</span>
      </span>
      <span v-if="rank" class="capsule-stats">
        <span class="stat-item" title="点赞">
          <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
          {{ rank.voteCount }}
        </span>
        <span class="stat-item" title="收藏">
          <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
            <path d="M17 3H7c-1.1 0-1.99.9-1.99 2L5 21l7-3 7 3V5c0-1.1-.9-2-2-2z"/>
          </svg>
          {{ rank.loveCount }}
        </span>
      </span>
    </span>
  </button>
</template>

<style scoped>
.cosmic-capsule {
  position: relative;
  display: flex;
  align-items: stretch;
  width: 100%;
  height: 100%;
  padding: 0;
  margin: 0;
  border: none;
  border-radius: 28px;
  overflow: hidden;
  background: transparent;
  text-align: left;
  cursor: default;
  outline: none;
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.04),
    0 8px 24px rgba(0, 0, 0, 0.06);
  transition: transform 0.35s cubic-bezier(0.22, 1, 0.36, 1),
    box-shadow 0.35s cubic-bezier(0.22, 1, 0.36, 1);
}

.cosmic-capsule.is-clickable {
  cursor: pointer;
}

.cosmic-capsule.is-clickable:hover {
  transform: translateY(-4px) scale(1.01);
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.1),
    0 4px 12px rgba(0, 0, 0, 0.05);
}

.cosmic-capsule:focus-visible {
  box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.08);
}

.capsule-visual {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.capsule-visual canvas {
  display: block;
  width: 100%;
  height: 100%;
}

.capsule-overlay {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 100%;
  padding: 18px 20px;
  pointer-events: none;
}

.capsule-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.capsule-code {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.08em;
  opacity: 0.55;
}

.capsule-live {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.12em;
  padding: 3px 7px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.22);
  backdrop-filter: blur(4px);
}

.capsule-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: auto;
}

.capsule-name {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.15;
  letter-spacing: -0.02em;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
}

.capsule-author {
  font-size: 13px;
  font-weight: 500;
  opacity: 0.72;
}

.capsule-stats {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 10px;
}

.stat-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  font-weight: 600;
  opacity: 0.8;
}

/* 深色主题（POLAR） */
.cosmic-capsule.dark {
  color: rgba(255, 255, 255, 0.92);
}

.cosmic-capsule.dark .capsule-live {
  background: rgba(255, 255, 255, 0.12);
}

/* 浅色主题 */
.cosmic-capsule.light {
  color: rgba(30, 30, 35, 0.88);
}

/* 尺寸变体 */
.capsule-small {
  border-radius: 20px;
}

.capsule-small .capsule-overlay {
  padding: 12px 14px;
}

.capsule-small .capsule-name {
  font-size: 15px;
}

.capsule-small .capsule-author {
  font-size: 11px;
}

.capsule-large {
  border-radius: 36px;
}

.capsule-large .capsule-overlay {
  padding: 26px 30px;
}

.capsule-large .capsule-name {
  font-size: 32px;
}

.capsule-hero {
  border-radius: 48px;
}

.capsule-hero .capsule-overlay {
  padding: 38px 42px;
}

.capsule-hero .capsule-name {
  font-size: 42px;
}

.capsule-hero .capsule-author {
  font-size: 18px;
}
</style>
