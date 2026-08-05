import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api

export interface RankItem {
  rankId: number
  rankName: string
  username: string
  voteCount: number
  loveCount: number
  presetIndex: number
}

export interface RankDetail {
  rankId: number
  rankName: string
  username: string
  songs: Array<{
    rank: number
    name: string
    artist: string
    votes: number
  }>
  aiComment: string
  comments: Array<{
    id: number
    username: string
    content: string
    time: string
  }>
}

export const rankApi = {
  list: (category?: string) => api.get<RankItem[]>('/chart/rank/list', { params: { category } }),
  search: (keyword: string, userId = 1) => api.get<RankItem[]>('/chart/rank/search', { params: { keyword, userId } }),
  detail: (id: number) => api.get<RankDetail>(`/chart/rank/${id}`),
  comments: (id: number, userId = 1) => api.get(`/chart/rank/${id}/comments`, { params: { userId } }),
  comment: (id: number, content: string) => api.post(`/chart/rank/${id}/comment`, { content })
}

export const userApi = {
  login: (username: string, password: string) =>
    api.post<{ token: string }>('/user/login', { username, password }),
  profile: () => api.get('/user/profile')
}
