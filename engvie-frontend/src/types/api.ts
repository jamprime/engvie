export interface ApiError {
  error: string
  message?: string
  status?: number
}

export interface PaginatedResponse<T> {
  data: T[]
  total: number
  page: number
  limit: number
}

export interface Achievement {
  id: number
  code: string
  nameEn: string
  nameRu: string
  descriptionRu?: string
  icon: string
  rewardCoins: number
  conditionType: string
  conditionValue: number
  unlocked: boolean
  unlockedAt?: string
}

export interface DailyTask {
  id: number
  date: string
  taskType: string
  requirement: number
  rewardCoins: number
  progress: number
  completed: boolean
  completedAt?: string
}

export interface LeaderboardEntry {
  rank: number
  userId: number
  username: string
  rating: number
  rankTitle: string
  wins: number
}
