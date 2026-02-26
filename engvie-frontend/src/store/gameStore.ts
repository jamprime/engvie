import { create } from 'zustand'
import { Game, GameQuestion, GameFinishedEvent } from '../types/game'

interface ActiveRound {
  roundId: number
  question: GameQuestion
  timeLimit: number
  startedAt: number
}

interface OpponentAnswer {
  playerId: number  // 0 = bot
  isCorrect: boolean
}

interface GameStore {
  activeGame: Game | null
  activeRound: ActiveRound | null
  currentScore: { player1: number; player2: number }
  gameFinished: GameFinishedEvent | null
  isMyTurn: boolean
  lastAnswerResult: { isCorrect: boolean; correctAnswer: string } | null
  opponentAnswer: OpponentAnswer | null

  setActiveGame: (game: Game | null) => void
  setActiveRound: (round: ActiveRound | null) => void
  setCurrentScore: (score: { player1: number; player2: number }) => void
  setGameFinished: (event: GameFinishedEvent | null) => void
  setIsMyTurn: (isMyTurn: boolean) => void
  setLastAnswerResult: (result: { isCorrect: boolean; correctAnswer: string } | null) => void
  setOpponentAnswer: (answer: OpponentAnswer | null) => void
  resetGame: () => void
}

export const useGameStore = create<GameStore>((set) => ({
  activeGame: null,
  activeRound: null,
  currentScore: { player1: 0, player2: 0 },
  gameFinished: null,
  isMyTurn: false,
  lastAnswerResult: null,
  opponentAnswer: null,

  setActiveGame: (game) => set({ activeGame: game }),
  setActiveRound: (round) => set({ activeRound: round }),
  setCurrentScore: (score) => set({ currentScore: score }),
  setGameFinished: (event) => set({ gameFinished: event }),
  setIsMyTurn: (isMyTurn) => set({ isMyTurn }),
  setLastAnswerResult: (result) => set({ lastAnswerResult: result }),
  setOpponentAnswer: (answer) => set({ opponentAnswer: answer }),
  resetGame: () =>
    set({
      activeGame: null,
      activeRound: null,
      currentScore: { player1: 0, player2: 0 },
      gameFinished: null,
      isMyTurn: false,
      lastAnswerResult: null,
      opponentAnswer: null,
    }),
}))
