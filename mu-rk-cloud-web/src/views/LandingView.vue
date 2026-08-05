<script setup lang="ts">
import { useRouter } from 'vue-router'
import CosmicCapsule from '@/components/CosmicCapsule.vue'
import type { CapsulePreset } from '@/utils/presets'

const router = useRouter()

const singerPreset: CapsulePreset = {
  id: 'hero-singer',
  code: 'HS-01',
  name: '歌手明星',
  group: 'warm',
  mode: 'nebula',
  seed: 18.5,
  speed: 0.38,
  colors: ['#FFF8F5', '#FAD4D4', '#F8B4B4', '#E7A3A3']
}

const workPreset: CapsulePreset = {
  id: 'hero-work',
  code: 'HW-01',
  name: '影视游戏',
  group: 'cold',
  mode: 'nebula',
  seed: 62.3,
  speed: 0.40,
  colors: ['#F5FAFF', '#D4E8F5', '#A8D4F0', '#8FBDD9']
}

const enterSinger = () => {
  router.push('/home?category=singer')
}

const enterWork = () => {
  router.push('/home?category=work')
}
</script>

<template>
  <main class="landing-page">
    <header class="landing-header">
      <div class="brand-block">
        <p class="micro-label">GENERATIVE RANK COMMUNITY</p>
        <h1>画境观屿</h1>
        <p class="brand-subtitle">RANK CAPSULES</p>
      </div>
      <p class="landing-intro">
        发现、创建与分享你心中的排行榜。每一颗胶囊都是一段由用户定义的排名宇宙。
      </p>
    </header>

    <section class="hero-grid" aria-label="入口选择">
      <div class="hero-card" @click="enterSinger">
        <CosmicCapsule
          :preset="singerPreset"
          size="hero"
          :show-info="false"
          :clickable="false"
        />
        <div class="hero-overlay">
          <span class="hero-badge">SINGER & STAR</span>
          <h2>歌手明星</h2>
          <p>为你喜爱的歌手、音乐人与明星创建专属榜单</p>
        </div>
      </div>

      <div class="hero-card" @click="enterWork">
        <CosmicCapsule
          :preset="workPreset"
          size="hero"
          :show-info="false"
          :clickable="false"
        />
        <div class="hero-overlay">
          <span class="hero-badge">MOVIE & GAME</span>
          <h2>影视游戏</h2>
          <p>电影、剧集、动漫与游戏作品的终极排行</p>
        </div>
      </div>
    </section>

    <footer class="landing-footer">
      <button class="login-link" @click="$router.push('/login')">
        已有账号？登录
      </button>
    </footer>
  </main>
</template>

<style scoped>
.landing-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: #faf9f7;
  color: #1e1e23;
}

.landing-header {
  text-align: center;
  margin-bottom: 48px;
}

.brand-block {
  margin-bottom: 20px;
}

.micro-label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.18em;
  color: rgba(30, 30, 35, 0.38);
  margin-bottom: 12px;
}

.brand-block h1 {
  font-size: 52px;
  font-weight: 900;
  letter-spacing: -0.04em;
  line-height: 1;
  margin-bottom: 8px;
}

.brand-subtitle {
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.2em;
  color: rgba(30, 30, 35, 0.4);
}

.landing-intro {
  max-width: 520px;
  font-size: 16px;
  line-height: 1.7;
  color: rgba(30, 30, 35, 0.6);
}

.hero-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(280px, 460px));
  gap: 32px;
  width: 100%;
  max-width: 1000px;
}

.hero-card {
  position: relative;
  height: 420px;
  border-radius: 48px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.4s cubic-bezier(0.22, 1, 0.36, 1);
}

.hero-card:hover {
  transform: translateY(-6px) scale(1.015);
}

.hero-card :deep(.cosmic-capsule) {
  border-radius: 48px;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 42px;
  color: rgba(30, 30, 35, 0.9);
  pointer-events: none;
  background: linear-gradient(to top, rgba(255, 255, 255, 0.55) 0%, rgba(255, 255, 255, 0) 60%);
}

.hero-badge {
  display: inline-block;
  width: fit-content;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.12em;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(6px);
  margin-bottom: 14px;
}

.hero-overlay h2 {
  font-size: 38px;
  font-weight: 800;
  letter-spacing: -0.03em;
  margin-bottom: 8px;
}

.hero-overlay p {
  font-size: 15px;
  line-height: 1.5;
  color: rgba(30, 30, 35, 0.65);
  max-width: 320px;
}

.landing-footer {
  margin-top: 48px;
}

.login-link {
  font-size: 14px;
  font-weight: 600;
  color: rgba(30, 30, 35, 0.55);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: color 0.2s ease;
}

.login-link:hover {
  color: #1e1e23;
}

@media (max-width: 900px) {
  .hero-grid {
    grid-template-columns: 1fr;
  }

  .hero-card {
    height: 320px;
  }
}
</style>
