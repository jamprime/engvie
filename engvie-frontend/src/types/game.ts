export interface Game {
  id: number
  player1: PlayerInfo
  player2?: PlayerInfo
  player1Score: number
  player2Score: number
  winnerId?: number
  gameType: string
  roundsCount: number
  status: string
  currentTurn?: number
  isBotGame: boolean
  botDifficulty?: string
  startedAt?: string
  finishedAt?: string
  createdAt: string
}

export interface PlayerInfo {
  id: number
  username: string
  rating: number
  rank: string
}

export interface GameQuestion {
  word: string
  direction: 'en_to_ru' | 'ru_to_en'
  options: string[]
}

export interface RoundResult {
  roundId: number
  isCorrect: boolean
  correctAnswer: string
  currentScore: { player1: number; player2: number }
}

export interface GameFinishedEvent {
  gameId: number
  result: 'win' | 'loss' | 'tie'
  finalScore: { player1: number; player2: number }
  ratingChange: number
  coinsEarned: number
  mistakes: Mistake[]
}

export interface Mistake {
  wordId: number
  word: string
  correctAnswer: string
}

export type Difficulty = 'easy' | 'medium' | 'hard'
