<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import SideNav from '@/components/SideNav.vue'
import CosmicCapsule from '@/components/CosmicCapsule.vue'
import { rankApi, type RankItem } from '@/api'
import type { CapsulePreset } from '@/utils/presets'

const router = useRouter()
const paused = ref(false)
const filter = ref<'all' | 'warm' | 'cold' | 'neutral'>('all')
const loading = ref(false)

// 榜单数据：优先从后端 /api/rank/list 获取，失败时使用模拟数据
const rankList = ref<RankItem[]>([])

const loadRanks = async () => {
  loading.value = true
  try {
    const { data } = await rankApi.list()
    rankList.value = data.map((item, index) => ({
      ...item,
      presetIndex: index % 9
    }))
  } catch (error) {
    console.warn('[HomeView] 后端接口调用失败，使用模拟数据', error)
    const rankNames = [
      '华语乐坛最佳男歌手', '2024 年度电影 Top10', '二次元神曲排行榜',
      '独立音乐人心头好', '经典摇滚永不熄灭', '治愈系女声榜单',
      '游戏 OST 巅峰对决', 'K-pop 神颜大赏', '民谣诗人自选集',
      '电子音乐入坑指南', '动漫 OP 封神榜', '欧美流行年度精选',
      '影视反派魅力榜', '国漫崛起代表作', '怀旧金曲 90 年代',
      'S级神仙打架现场', '游戏角色人气王', 'Sleep 必备轻音乐'
    ]
    rankList.value = Array.from({ length: 18 }, (_, i) => ({
      rankId: i + 1,
      rankName: rankNames[i] ?? '未命名榜单',
      username: `user_${(i % 6) + 1}`,
      voteCount: Math.floor(Math.random() * 5000),
      loveCount: Math.floor(Math.random() * 3000),
      presetIndex: i % 9
    }))
  } finally {
    loading.value = false
  }
}

onMounted(loadRanks)

const filteredRanks = computed(() => {
  if (filter.value === 'all') return rankList.value
  return rankList.value.filter((rank) => {
    const preset: CapsulePreset = {
      id: '',
      code: '',
      name: '',
      group: 'warm',
      seed: 0,
      speed: 0,
      colors: []
    }
    // 这里根据预设颜色分组，简单按索引映射
    const groups = ['warm', 'warm', 'warm', 'cold', 'cold', 'cold', 'warm', 'cold', 'neutral']
    return groups[rank.presetIndex] === filter.value
  })
})

const visibleCount = computed(() => String(Math.min(filteredRanks.value.length, 9)).padStart(2, '0'))
const totalCount = computed(() => String(filteredRanks.value.length).padStart(2, '0'))

const openRankDetail = (rank: { rankId: number } | undefined) => {
  if (!rank) return
  router.push(`/rank/${rank.rankId}`)
}

const togglePause = () => {
  paused.value = !paused.value
  // 后续可通过 provide/inject 或全局状态同步暂停
}
</script>

<template>
  <div class="home-layout">
    <SideNav />

    <main class="home-main">
      <header class="home-header">
        <div class="brand-block">
          <p class="micro-label">GENERATIVE RANK COMMUNITY</p>
          <h1>画境观屿</h1>
          <p class="brand-subtitle">RANK CAPSULES</p>
        </div>

        <div class="header-actions">
          <button class="text-action" type="button" @click="togglePause">
            {{ paused ? '继续动效' : '暂停动效' }}
          </button>
          <button class="primary-action" type="button" @click="$router.push('/login')">
            登录 / 注册
          </button>
        </div>
      </header>

      <section class="content-layout">
        <aside class="intro-panel">
          <p class="section-kicker">RANK AS A LIVING SYSTEM.</p>
          <h2>让固定的榜单，<br />拥有会呼吸的宇宙。</h2>
          <p class="intro-copy">
            每一颗胶囊都是用户创建的榜单。点击进入，查看排名、发表评论，并体验 AI 锐评。
          </p>

          <div class="filters" role="group" aria-label="榜单筛选">
            <button
              class="filter-button"
              :class="{ 'is-active': filter === 'all' }"
              @click="filter = 'all'"
            >
              全部
            </button>
            <button
              class="filter-button"
              :class="{ 'is-active': filter === 'warm' }"
              @click="filter = 'warm'"
            >
              暖调
            </button>
            <button
              class="filter-button"
              :class="{ 'is-active': filter === 'cold' }"
              @click="filter = 'cold'"
            >
              冷调
            </button>
            <button
              class="filter-button"
              :class="{ 'is-active': filter === 'neutral' }"
              @click="filter = 'neutral'"
            >
              中性
            </button>
          </div>

          <p class="capsule-count">
            <span>{{ visibleCount }}</span> / {{ totalCount }}
          </p>
        </aside>

        <div class="capsule-grid" aria-live="polite">
          <CosmicCapsule
            v-for="(rank, index) in filteredRanks.slice(0, 9)"
            :key="rank.rankId"
            :index="rank.presetIndex"
            :rank="rank"
            size="medium"
            @click="openRankDetail"
          />
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.home-layout {
  display: flex;
  min-height: 100vh;
  background: #faf9f7;
  color: #1e1e23;
}

.home-main {
  flex: 1;
  margin-left: 220px;
  padding: 40px 48px 80px;
}

.home-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 48px;
}

.brand-block {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.micro-label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.18em;
  color: rgba(30, 30, 35, 0.38);
}

.brand-block h1 {
  font-size: 42px;
  font-weight: 900;
  letter-spacing: -0.04em;
  line-height: 1;
}

.brand-subtitle {
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.18em;
  color: rgba(30, 30, 35, 0.4);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.text-action {
  font-size: 14px;
  font-weight: 600;
  color: rgba(30, 30, 35, 0.55);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: color 0.2s ease;
}

.text-action:hover {
  color: #1e1e23;
}

.primary-action {
  font-size: 14px;
  font-weight: 700;
  padding: 12px 22px;
  border-radius: 14px;
  border: none;
  background: #1e1e23;
  color: #fff;
  cursor: pointer;
  transition: transform 0.2s ease, background 0.2s ease;
}

.primary-action:hover {
  transform: translateY(-2px);
  background: #33333a;
}

.content-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 48px;
  align-items: start;
}

.intro-panel {
  position: sticky;
  top: 40px;
}

.section-kicker {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.12em;
  color: rgba(30, 30, 35, 0.38);
  margin-bottom: 14px;
}

.intro-panel h2 {
  font-size: 28px;
  font-weight: 800;
  line-height: 1.2;
  letter-spacing: -0.02em;
  margin-bottom: 16px;
}

.intro-copy {
  font-size: 15px;
  line-height: 1.7;
  color: rgba(30, 30, 35, 0.58);
  margin-bottom: 28px;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 28px;
}

.filter-button {
  font-size: 13px;
  font-weight: 700;
  padding: 10px 18px;
  border-radius: 999px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.55);
  color: rgba(30, 30, 35, 0.55);
  cursor: pointer;
  transition: all 0.25s ease;
}

.filter-button:hover {
  background: rgba(255, 255, 255, 0.85);
  color: rgba(30, 30, 35, 0.85);
}

.filter-button.is-active {
  background: #1e1e23;
  color: #fff;
  border-color: #1e1e23;
}

.capsule-count {
  font-size: 13px;
  font-weight: 600;
  color: rgba(30, 30, 35, 0.45);
}

.capsule-count span {
  font-size: 32px;
  font-weight: 800;
  color: #1e1e23;
  margin-right: 6px;
}

.capsule-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-auto-rows: 220px;
  gap: 22px;
}

@media (max-width: 1200px) {
  .capsule-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 900px) {
  .home-main {
    margin-left: 0;
    padding: 24px 20px 100px;
  }

  .content-layout {
    grid-template-columns: 1fr;
  }

  .intro-panel {
    position: static;
  }

  .capsule-grid {
    grid-template-columns: 1fr;
    grid-auto-rows: 240px;
  }
}
</style>
