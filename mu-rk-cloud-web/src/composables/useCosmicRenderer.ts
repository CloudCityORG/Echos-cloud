import { onMounted, onUnmounted, ref, watch } from 'vue'
import { CosmicRenderer } from '@/utils/cosmic-shader'
import { FallbackRenderer } from '@/utils/fallback'
import type { CapsulePreset } from '@/utils/presets'

export type RendererInstance = CosmicRenderer | FallbackRenderer

export interface UseCosmicRendererOptions {
  dprCap?: number
  paused?: boolean
}

export function useCosmicRenderer(
  canvasRef: { value: HTMLCanvasElement | null },
  preset: () => CapsulePreset,
  options: UseCosmicRendererOptions = {}
) {
  const renderer = ref<RendererInstance | null>(null)
  let rafId = 0
  let lastTime = performance.now()
  let animationTime = 0

  const createRenderer = (canvas: HTMLCanvasElement, p: CapsulePreset) => {
    try {
      return new CosmicRenderer(canvas, p, { dprCap: options.dprCap ?? 1.75 })
    } catch (error) {
      console.warn('[CosmicCapsule] WebGL2 不可用，已切换 Canvas 2D 降级动效。', error)
      return new FallbackRenderer(canvas, p)
    }
  }

  const start = () => {
    if (rafId) return
    lastTime = performance.now()
    const loop = (now: number) => {
      const delta = Math.min((now - lastTime) / 1000, 0.05)
      lastTime = now
      if (!options.paused) animationTime += delta
      renderer.value?.draw(animationTime, options.paused)
      rafId = requestAnimationFrame(loop)
    }
    rafId = requestAnimationFrame(loop)
  }

  const stop = () => {
    if (rafId) {
      cancelAnimationFrame(rafId)
      rafId = 0
    }
  }

  onMounted(() => {
    if (!canvasRef.value) return
    renderer.value = createRenderer(canvasRef.value, preset())
    start()
  })

  watch(
    () => preset(),
    (newPreset) => {
      renderer.value?.setPreset(newPreset)
    },
    { deep: true }
  )

  onUnmounted(() => {
    stop()
    renderer.value?.dispose()
    renderer.value = null
  })

  return {
    renderer,
    randomize: () => renderer.value?.randomize(),
    start,
    stop
  }
}
