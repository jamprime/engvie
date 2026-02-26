import { useCallback, useEffect } from 'react'
import { useGameStore } from '../store'
import { useUserStore } from '../store'
import { wsService } from '../services/websocket'

export function useGame(gameId: number | null) {
  const {
    activeGame,
    activeRound,
    currentScore,
    gameFinished,
    isMyTurn,
    lastAnswerResult,
    setActiveRound,
    setCurrentScore,
    setGameFinished,
    setIsMyTurn,
    setLastAnswerResult,
    resetGame,
  } = useGameStore()
  const { user } = useUserStore()

  useEffect(() => {
    if (!gameId || !user) return

    // Subscribe to game events
    wsService.subscribe(`/topic/game/${gameId}`, (data) => {
      if (data.type === 'OPPONENT_ANSWERED') {
        // Opponent answered - show waiting state
      } else if (data.type === 'GAME_FINISHED') {
        setGameFinished(data)
      }
    })

    wsService.subscribe(`/user/queue/game`, (data) => {
      if (data.type === 'ROUND_START') {
        setActiveRound({
          roundId: data.roundId,
          question: data.question,
          timeLimit: data.timeLimit,
          startedAt: Date.now(),
        })
        setIsMyTurn(data.playerId === user.id)
      } else if (data.type === 'ANSWER_RESULT') {
        setLastAnswerResult({
          isCorrect: data.isCorrect,
          correctAnswer: data.correctAnswer,
        })
        setCurrentScore(data.currentScore)
      } else if (data.type === 'GAME_FINISHED') {
        setGameFinished(data)
      }
    })

    // Join the game
    wsService.send('/game.join', { gameId })

    return () => {
      wsService.unsubscribe(`/topic/game/${gameId}`)
      wsService.unsubscribe(`/user/queue/game`)
    }
  }, [gameId, user?.id])

  const submitAnswer = useCallback(
    (roundId: number, answer: string, answerTimeMs: number) => {
      if (!gameId) return
      wsService.send('/game.answer', {
        gameId,
        roundId,
        answer,
        answerTimeMs,
      })
    },
    [gameId]
  )

  return {
    activeGame,
    activeRound,
    currentScore,
    gameFinished,
    isMyTurn,
    lastAnswerResult,
    submitAnswer,
    resetGame,
  }
}
