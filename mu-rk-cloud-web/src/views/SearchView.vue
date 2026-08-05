<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SideNav from '@/components/SideNav.vue'

const route = useRoute()
const router = useRouter()
const keyword = ref(String(route.query.q || ''))

const search = () => {
  if (!keyword.value.trim()) return
  router.push({ path: '/search', query: { q: keyword.value.trim() } })
}
</script>

<template>
  <div class="search-layout">
    <SideNav />
    <main class="search-main">
      <header class="search-header">
        <h1>搜索榜单</h1>
        <p>输入关键词，发现你感兴趣的榜单宇宙。</p>
      </header>

      <div class="search-box">
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索榜单、歌曲、用户..."
          @keydown.enter="search"
        />
        <button type="button" @click="search">搜索</button>
      </div>

      <section class="search-results">
        <p class="placeholder">输入关键词开始搜索</p>
      </section>
    </main>
  </div>
</template>

<style scoped>
.search-layout {
  display: flex;
  min-height: 100vh;
  background: #faf9f7;
  color: #1e1e23;
}

.search-main {
  flex: 1;
  margin-left: 220px;
  padding: 48px;
}

.search-header {
  margin-bottom: 36px;
}

.search-header h1 {
  font-size: 36px;
  font-weight: 900;
  letter-spacing: -0.03em;
  margin-bottom: 10px;
}

.search-header p {
  color: rgba(30, 30, 35, 0.55);
}

.search-box {
  display: flex;
  gap: 12px;
  margin-bottom: 40px;
}

.search-box input {
  flex: 1;
  max-width: 600px;
  padding: 16px 22px;
  font-size: 16px;
  border-radius: 18px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.7);
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.search-box input:focus {
  border-color: rgba(30, 30, 35, 0.25);
  box-shadow: 0 0 0 4px rgba(30, 30, 35, 0.04);
}

.search-box button {
  padding: 16px 32px;
  font-size: 15px;
  font-weight: 700;
  border-radius: 18px;
  border: none;
  background: #1e1e23;
  color: #fff;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.search-box button:hover {
  transform: translateY(-2px);
}

.search-results .placeholder {
  text-align: center;
  color: rgba(30, 30, 35, 0.4);
  padding: 80px 0;
}

@media (max-width: 900px) {
  .search-main {
    margin-left: 0;
    padding: 24px 20px 100px;
  }
}
</style>
