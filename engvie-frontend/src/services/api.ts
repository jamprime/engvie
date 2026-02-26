import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_URL || '/api/v1'

export const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('auth_token')
      localStorage.removeItem('engvie-user') // Clear Zustand persist so expired token isn't restored
      window.location.href = '/'
    }
    return Promise.reject(error)
  }
)

// Separate axios instance for auth — no 401 interceptor (auth endpoint legitimately returns 401 on bad initData)
const authAxios = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Auth
export const authApi = {
  telegram: (initData: string) =>
    authAxios.post('/auth/telegram', { initData }),
}

// User
export const userApi = {
  getMe: () => api.get('/users/me'),
  getStats: () => api.get('/users/me/stats'),
  buyEnergy: () => api.post('/users/me/buy-energy'),
}

// Games
export const gamesApi = {
  getHistory: (page = 1, limit = 20) =>
    api.get(`/games/history?page=${page}&limit=${limit}`),
  getGame: (gameId: number) => api.get(`/games/${gameId}`),
  startComputerGame: (difficulty: string, roundsCount: number) =>
    api.post('/games/computer', { difficulty, roundsCount }),
}

// Matchmaking
export const matchmakingApi = {
  join: (roundsCount: number) =>
    api.post('/matchmaking/join', { roundsCount }),
  cancel: () => api.delete('/matchmaking/cancel'),
}

// Learning
export const learningApi = {
  getMyWords: (status?: string, category?: string) => {
    const params = new URLSearchParams()
    if (status) params.append('status', status)
    if (category) params.append('category', category)
    return api.get(`/words/my?${params}`)
  },
  addWord: (wordId: number, addedFrom = 'manual') =>
    api.post(`/words/my/${wordId}?addedFrom=${addedFrom}`),
  removeWord: (wordId: number) => api.delete(`/words/my/${wordId}`),
  getDueWords: () => api.get('/words/due'),
  reviewWord: (wordId: number, quality: number) =>
    api.post(`/words/${wordId}/review`, { quality }),
  getCategories: () => api.get('/words/categories'),
  getCategoryWords: (code: string) => api.get(`/words/categories/${code}`),
  getMistakes: () => api.get('/words/mistakes'),
}

// Achievements
export const achievementsApi = {
  getAll: () => api.get('/achievements'),
}

// Daily Tasks
export const dailyTasksApi = {
  getAll: () => api.get('/daily-tasks'),
  claim: (taskId: number) => api.post(`/daily-tasks/${taskId}/claim`),
}

// Leaderboard
export const leaderboardApi = {
  getGlobal: () => api.get('/leaderboard/global'),
}
