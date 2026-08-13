<script setup lang="ts">
import SideNav from '@/components/SideNav.vue'
</script>

<template>
  <div class="profile-layout">
    <SideNav />
    <main class="profile-main">
      <header class="profile-header">
        <div class="avatar">
          <span>U</span>
        </div>
        <div class="profile-info">
          <h1>用户名</h1>
          <p>@user_001</p>
        </div>
      </header>

      <section class="profile-stats">
        <div class="stat-card">
          <span class="stat-value">12</span>
          <span class="stat-label">创建的榜单</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">48</span>
          <span class="stat-label">收藏</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">256</span>
          <span class="stat-label">获赞</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">35</span>
          <span class="stat-label">关注</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">102</span>
          <span class="stat-label">粉丝</span>
        </div>
      </section>

      <section class="profile-tabs">
        <h2>我的榜单</h2>
        <p class="empty">暂无榜单，去首页创建一个吧。</p>
      </section>
    </main>
  </div>
</template>

<style scoped>
.profile-layout {
  display: flex;
  min-height: 100vh;
  background: #faf9f7;
  color: #1e1e23;
}

.profile-main {
  flex: 1;
  margin-left: 220px;
  padding: 48px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 36px;
}

.avatar {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5b27a 0%, #f67bc6 50%, #a978e8 100%);
  color: #fff;
  font-size: 40px;
  font-weight: 800;
  box-shadow: 0 8px 28px rgba(169, 120, 232, 0.22);
}

.profile-info h1 {
  font-size: 32px;
  font-weight: 900;
  letter-spacing: -0.02em;
  margin-bottom: 6px;
}

.profile-info p {
  color: rgba(30, 30, 35, 0.5);
  font-size: 15px;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
  margin-bottom: 40px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 20px;
  padding: 22px;
  text-align: center;
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: rgba(30, 30, 35, 0.55);
}

.profile-tabs h2 {
  font-size: 22px;
  font-weight: 800;
  margin-bottom: 20px;
}

.profile-tabs .empty {
  color: rgba(30, 30, 35, 0.45);
  padding: 40px 0;
  text-align: center;
}

@media (max-width: 900px) {
  .profile-main {
    margin-left: 0;
    padding: 24px 20px 100px;
  }
}
</style>
