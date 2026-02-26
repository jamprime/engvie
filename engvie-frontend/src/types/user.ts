export interface User {
  id: number
  username: string
  firstName: string
  lastName?: string
  rating: number
  rank: string
  level: string
  totalGames: number
  wins: number
  losses: number
  draws: number
  energy: number
  maxEnergy: number
  nextEnergyInSeconds: number | null  // seconds until next ⚡, null when full
  coins: number
  streakDays: number
}

export interface UserStats {
  totalGames: number
  wins: number
  losses: number
  draws: number
  winRate: number
  avgAnswerTimeMs?: number
}
