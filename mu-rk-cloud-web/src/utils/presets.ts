export interface CapsulePreset {
  id: string
  code: string
  name: string
  subtitle?: string
  group: 'warm' | 'cold' | 'neutral'
  mode?: 'nebula' | 'aurora'
  motionProfile?: 'polar' | 'dubdot' | 'vercel'
  theme?: 'light' | 'dark'
  seed: number
  speed: number
  colors: string[]
}

/**
 * 榜单胶囊颜色预设：柔和低饱和，差异不大，统一奶油白背景风格
 */
export const RANK_PRESETS: CapsulePreset[] = [
  {
    id: 'cream-blush',
    code: 'RC-01',
    name: '奶油粉',
    group: 'warm',
    mode: 'nebula',
    seed: 12.3,
    speed: 0.42,
    colors: ['#FFF8F5', '#FAD4D4', '#F8B4B4', '#E7A3A3']
  },
  {
    id: 'cream-peach',
    code: 'RC-02',
    name: '奶油桃',
    group: 'warm',
    mode: 'nebula',
    seed: 24.7,
    speed: 0.45,
    colors: ['#FFFBF5', '#FCE4C4', '#F6C896', '#E5B07A']
  },
  {
    id: 'cream-lemon',
    code: 'RC-03',
    name: '奶油柠',
    group: 'warm',
    mode: 'nebula',
    seed: 38.1,
    speed: 0.40,
    colors: ['#FFFDF5', '#FAF0C4', '#F3E19A', '#D9C97E']
  },
  {
    id: 'cream-mint',
    code: 'RC-04',
    name: '奶油薄荷',
    group: 'cold',
    mode: 'nebula',
    seed: 51.6,
    speed: 0.44,
    colors: ['#F5FFFA', '#D4F5E9', '#A8E6CF', '#8CCCB0']
  },
  {
    id: 'cream-sky',
    code: 'RC-05',
    name: '奶油天蓝',
    group: 'cold',
    mode: 'nebula',
    seed: 65.2,
    speed: 0.43,
    colors: ['#F5FAFF', '#D4E8F5', '#A8D4F0', '#8FBDD9']
  },
  {
    id: 'cream-lavender',
    code: 'RC-06',
    name: '奶油薰衣草',
    group: 'cold',
    mode: 'nebula',
    seed: 78.9,
    speed: 0.46,
    colors: ['#FAF5FF', '#E5D4F5', '#CDB4F0', '#B39AD6']
  },
  {
    id: 'cream-polar',
    code: 'RC-07',
    name: '奶油暖橙',
    group: 'warm',
    mode: 'aurora',
    motionProfile: 'polar',
    theme: 'dark',
    seed: 92.4,
    speed: 0.32,
    colors: ['#2A2A2E', '#FF9F5A', '#FF6B9A', '#FFF5A8']
  },
  {
    id: 'cream-dubdot',
    code: 'RC-08',
    name: '奶油冰蓝',
    group: 'cold',
    mode: 'aurora',
    motionProfile: 'dubdot',
    theme: 'light',
    seed: 15.8,
    speed: 0.28,
    colors: ['#FFFFFF', '#E8F4FF', '#B8E0FF', '#6BC5FF']
  },
  {
    id: 'cream-vercel',
    code: 'RC-09',
    name: '奶油春彩',
    group: 'neutral',
    mode: 'aurora',
    motionProfile: 'vercel',
    theme: 'light',
    seed: 47.3,
    speed: 0.34,
    colors: ['#FFFFFF', '#D8F5F0', '#F5E6A8', '#FFB8D0']
  }
]

export const hexToRgb01 = (hex: string): number[] => {
  const normalized = hex.replace('#', '')
  const value = Number.parseInt(normalized, 16)
  return [((value >> 16) & 255) / 255, ((value >> 8) & 255) / 255, (value & 255) / 255]
}

export const getPresetByIndex = (index: number): CapsulePreset => {
  return RANK_PRESETS[index % RANK_PRESETS.length] ?? RANK_PRESETS[0]!
}
