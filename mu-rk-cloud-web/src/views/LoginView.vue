<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import CosmicCapsule from '@/components/CosmicCapsule.vue'
import type { CapsulePreset } from '@/utils/presets'

const router = useRouter()
const username = ref('')
const password = ref('')

const loginPreset: CapsulePreset = {
  id: 'login-capsule',
  code: 'LG-01',
  name: '登录',
  group: 'neutral',
  mode: 'aurora',
  motionProfile: 'vercel',
  theme: 'light',
  seed: 33.3,
  speed: 0.30,
  colors: ['#FFFFFF', '#D8F5F0', '#F5E6A8', '#FFB8D0']
}

const login = () => {
  // 后续对接 /api/user/login
  console.log('登录：', username.value, password.value)
  router.push('/home')
}
</script>

<template>
  <main class="login-page">
    <div class="login-visual">
      <CosmicCapsule :preset="loginPreset" size="hero" :show-info="false" :clickable="false" />
    </div>

    <div class="login-panel">
      <div class="login-brand">
        <p class="micro-label">GENERATIVE RANK COMMUNITY</p>
        <h1>画境观屿</h1>
        <p class="brand-subtitle">RANK CAPSULES</p>
      </div>

      <form class="login-form" @submit.prevent="login">
        <h2>欢迎回来</h2>
        <p class="login-subtitle">登录后继续探索榜单宇宙</p>

        <label>
          <span>用户名</span>
          <input v-model="username" type="text" placeholder="请输入用户名" required />
        </label>

        <label>
          <span>密码</span>
          <input v-model="password" type="password" placeholder="请输入密码" required />
        </label>

        <button type="submit" class="submit-button">登 录</button>

        <p class="login-actions">
          <button type="button" class="link-button">忘记密码？</button>
          <button type="button" class="link-button" @click="$router.push('/')">
            返回首页
          </button>
        </p>
      </form>
    </div>
  </main>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 1fr;
  background: #faf9f7;
  color: #1e1e23;
}

.login-visual {
  position: relative;
  min-height: 100vh;
  padding: 40px;
}

.login-visual :deep(.cosmic-capsule) {
  border-radius: 48px;
  height: 100%;
  min-height: calc(100vh - 80px);
}

.login-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 48px 80px;
}

.login-brand {
  margin-bottom: 48px;
}

.micro-label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.18em;
  color: rgba(30, 30, 35, 0.38);
  margin-bottom: 12px;
}

.login-brand h1 {
  font-size: 42px;
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

.login-form h2 {
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 8px;
}

.login-subtitle {
  color: rgba(30, 30, 35, 0.55);
  margin-bottom: 32px;
}

.login-form label {
  display: block;
  margin-bottom: 20px;
}

.login-form label span {
  display: block;
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 8px;
  color: rgba(30, 30, 35, 0.65);
}

.login-form input {
  width: 100%;
  padding: 16px 20px;
  font-size: 15px;
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.7);
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.login-form input:focus {
  border-color: rgba(30, 30, 35, 0.25);
  box-shadow: 0 0 0 4px rgba(30, 30, 35, 0.04);
}

.submit-button {
  width: 100%;
  padding: 16px 0;
  margin-top: 12px;
  font-size: 16px;
  font-weight: 800;
  border-radius: 16px;
  border: none;
  background: #1e1e23;
  color: #fff;
  cursor: pointer;
  transition: transform 0.2s ease, background 0.2s ease;
}

.submit-button:hover {
  transform: translateY(-2px);
  background: #33333a;
}

.login-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.link-button {
  font-size: 13px;
  font-weight: 600;
  color: rgba(30, 30, 35, 0.55);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: color 0.2s ease;
}

.link-button:hover {
  color: #1e1e23;
}

@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .login-visual {
    display: none;
  }

  .login-panel {
    padding: 48px 24px;
  }
}
</style>
