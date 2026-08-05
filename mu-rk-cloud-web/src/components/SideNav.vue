<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const navItems = [
  { name: '首页', path: '/', icon: 'home' },
  { name: '主页', path: '/home', icon: 'grid' },
  { name: '搜索', path: '/search', icon: 'search' },
  { name: '我的', path: '/profile', icon: 'user' }
]

const activeIndex = computed(() => {
  return navItems.findIndex((item) => route.path === item.path)
})

const navigate = (path: string) => {
  router.push(path)
}
</script>

<template>
  <aside class="side-nav">
    <div class="brand">
      <span class="brand-mark"></span>
      <div>
        <h1 class="brand-title">画境观屿</h1>
        <p class="brand-subtitle">RANK CAPSULES</p>
      </div>
    </div>

    <nav class="nav-menu" aria-label="主导航">
      <button
        v-for="(item, index) in navItems"
        :key="item.path"
        type="button"
        class="nav-item"
        :class="{ active: activeIndex === index }"
        @click="navigate(item.path)"
      >
        <span class="nav-icon">
          <svg v-if="item.icon === 'home'" viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
            <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
          </svg>
          <svg v-else-if="item.icon === 'grid'" viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
            <path d="M3 3h8v8H3zm10 0h8v8h-8zM3 13h8v8H3zm10 0h8v8h-8z"/>
          </svg>
          <svg v-else-if="item.icon === 'search'" viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
            <path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zM9.5 14C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
            <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
          </svg>
        </span>
        <span class="nav-label">{{ item.name }}</span>
      </button>
    </nav>

    <div class="nav-footer">
      <p>© 画境观屿</p>
    </div>
  </aside>
</template>

<style scoped>
.side-nav {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 220px;
  display: flex;
  flex-direction: column;
  padding: 32px 24px;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(16px);
  border-right: 1px solid rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 48px;
}

.brand-mark {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: linear-gradient(135deg, #f5b27a 0%, #f67bc6 50%, #a978e8 100%);
  box-shadow: 0 4px 14px rgba(169, 120, 232, 0.25);
}

.brand-title {
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.03em;
  line-height: 1.1;
  color: #1e1e23;
}

.brand-subtitle {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.14em;
  color: rgba(30, 30, 35, 0.42);
  margin-top: 3px;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 16px;
  border: none;
  background: transparent;
  color: rgba(30, 30, 35, 0.55);
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
}

.nav-item:hover {
  background: rgba(0, 0, 0, 0.03);
  color: rgba(30, 30, 35, 0.85);
}

.nav-item.active {
  background: rgba(30, 30, 35, 0.06);
  color: #1e1e23;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

.nav-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  opacity: 0.85;
}

.nav-footer {
  margin-top: auto;
  font-size: 11px;
  color: rgba(30, 30, 35, 0.35);
  text-align: center;
}

@media (max-width: 900px) {
  .side-nav {
    width: 100%;
    height: 64px;
    top: auto;
    bottom: 0;
    flex-direction: row;
    align-items: center;
    padding: 0 16px;
    border-right: none;
    border-top: 1px solid rgba(0, 0, 0, 0.05);
  }

  .brand,
  .nav-footer {
    display: none;
  }

  .nav-menu {
    flex-direction: row;
    justify-content: space-around;
    width: 100%;
  }

  .nav-item {
    flex-direction: column;
    gap: 4px;
    padding: 8px 12px;
    font-size: 11px;
  }

  .nav-icon svg {
    width: 22px;
    height: 22px;
  }
}
</style>
