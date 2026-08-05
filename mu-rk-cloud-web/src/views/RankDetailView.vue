<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import SideNav from '@/components/SideNav.vue'

const route = useRoute()
const rankId = Number(route.params.id)

const comments = ref([
  { id: 1, username: 'user_3', content: '这个榜单太真实了，第一实至名归！', time: '10 分钟前' },
  { id: 2, username: 'user_7', content: '我觉得第三应该排第一，个人意见。', time: '1 小时前' },
  { id: 3, username: 'user_2', content: '榜单整体不错，但缺少几首经典。', time: '3 小时前' }
])

const aiComment = ref(
  '这份榜单整体审美在线，前两名选择了大众口碑与专业评价兼具的作品，体现了作者对主流审美的精准把握。不过中段排名略显保守，若能加入一两首小众但质感突出的作品，会更有个人印记。'
)

const newComment = ref('')

const postComment = () => {
  if (!newComment.value.trim()) return
  comments.value.unshift({
    id: Date.now(),
    username: '我',
    content: newComment.value.trim(),
    time: '刚刚'
  })
  newComment.value = ''
}

// 模拟榜单排名数据
const songs = ref([
  { rank: 1, name: '晴天', artist: '周杰伦', votes: 3421 },
  { rank: 2, name: '夜曲', artist: '周杰伦', votes: 2890 },
  { rank: 3, name: '七里香', artist: '周杰伦', votes: 2654 },
  { rank: 4, name: '告白气球', artist: '周杰伦', votes: 2103 },
  { rank: 5, name: '稻香', artist: '周杰伦', votes: 1987 },
  { rank: 6, name: '青花瓷', artist: '周杰伦', votes: 1765 },
  { rank: 7, name: '等你下课', artist: '周杰伦', votes: 1543 },
  { rank: 8, name: '简单爱', artist: '周杰伦', votes: 1320 },
  { rank: 9, name: '说好的幸福呢', artist: '周杰伦', votes: 1102 }
])
</script>

<template>
  <div class="rank-layout">
    <SideNav />
    <main class="rank-main">
      <header class="rank-header">
        <button class="back-button" @click="$router.back()">← 返回</button>
        <div class="rank-title">
          <h1>华语乐坛最佳男歌手</h1>
          <p>@user_1 · 榜单 #{{ rankId }}</p>
        </div>
      </header>

      <div class="rank-body">
        <section class="rank-content">
          <h2>榜单排名</h2>
          <ul class="song-list">
            <li v-for="song in songs" :key="song.rank" class="song-item">
              <span class="rank-number">{{ song.rank }}</span>
              <div class="song-info">
                <span class="song-name">{{ song.name }}</span>
                <span class="song-artist">{{ song.artist }}</span>
              </div>
              <span class="song-votes">{{ song.votes }} 票</span>
            </li>
          </ul>
        </section>

        <aside class="rank-sidebar">
          <section class="ai-section">
            <h2>AI 锐评</h2>
            <div class="ai-card">
              <p>{{ aiComment }}</p>
            </div>
          </section>

          <section class="comment-section">
            <h2>评论区</h2>
            <div class="comment-input">
              <textarea v-model="newComment" placeholder="写下你的看法..." rows="3"></textarea>
              <button type="button" @click="postComment">发表评论</button>
            </div>
            <ul class="comment-list">
              <li v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-head">
                  <strong>@{{ comment.username }}</strong>
                  <span>{{ comment.time }}</span>
                </div>
                <p>{{ comment.content }}</p>
              </li>
            </ul>
          </section>
        </aside>
      </div>
    </main>
  </div>
</template>

<style scoped>
.rank-layout {
  display: flex;
  min-height: 100vh;
  background: #faf9f7;
  color: #1e1e23;
}

.rank-main {
  flex: 1;
  margin-left: 220px;
  padding: 40px 48px;
}

.rank-header {
  margin-bottom: 32px;
}

.back-button {
  font-size: 14px;
  font-weight: 600;
  color: rgba(30, 30, 35, 0.55);
  background: transparent;
  border: none;
  cursor: pointer;
  margin-bottom: 16px;
  transition: color 0.2s ease;
}

.back-button:hover {
  color: #1e1e23;
}

.rank-title h1 {
  font-size: 34px;
  font-weight: 900;
  letter-spacing: -0.03em;
  margin-bottom: 8px;
}

.rank-title p {
  color: rgba(30, 30, 35, 0.5);
}

.rank-body {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 40px;
  align-items: start;
}

.rank-content,
.ai-card,
.comment-input,
.comment-item {
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 24px;
}

.rank-content h2,
.ai-section h2,
.comment-section h2 {
  font-size: 20px;
  font-weight: 800;
  margin-bottom: 18px;
}

.song-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.song-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  transition: background 0.2s ease;
}

.song-item:last-child {
  border-bottom: none;
}

.song-item:hover {
  background: rgba(0, 0, 0, 0.02);
}

.rank-number {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #1e1e23;
  color: #fff;
  font-size: 14px;
  font-weight: 800;
}

.song-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.song-name {
  font-size: 16px;
  font-weight: 700;
}

.song-artist {
  font-size: 13px;
  color: rgba(30, 30, 35, 0.5);
}

.song-votes {
  font-size: 13px;
  font-weight: 700;
  color: rgba(30, 30, 35, 0.55);
}

.rank-sidebar {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.ai-card {
  padding: 22px;
}

.ai-card p {
  font-size: 15px;
  line-height: 1.7;
  color: rgba(30, 30, 35, 0.75);
}

.comment-input {
  padding: 18px;
  margin-bottom: 18px;
}

.comment-input textarea {
  width: 100%;
  padding: 12px;
  font-size: 14px;
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.6);
  resize: vertical;
  outline: none;
  margin-bottom: 12px;
}

.comment-input textarea:focus {
  border-color: rgba(30, 30, 35, 0.2);
}

.comment-input button {
  width: 100%;
  padding: 12px 0;
  font-size: 14px;
  font-weight: 700;
  border-radius: 12px;
  border: none;
  background: #1e1e23;
  color: #fff;
  cursor: pointer;
}

.comment-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-item {
  padding: 16px 18px;
}

.comment-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-head strong {
  font-size: 14px;
}

.comment-head span {
  font-size: 12px;
  color: rgba(30, 30, 35, 0.45);
}

.comment-item p {
  font-size: 14px;
  line-height: 1.6;
  color: rgba(30, 30, 35, 0.72);
}

@media (max-width: 1100px) {
  .rank-body {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .rank-main {
    margin-left: 0;
    padding: 24px 20px 100px;
  }
}
</style>
