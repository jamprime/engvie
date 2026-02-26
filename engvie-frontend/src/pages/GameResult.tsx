import React, { useEffect, useRef } from 'react'
import { useNavigate, useLocation, useParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Button } from '../components/common/Button'
import { useUserStore } from '../store'
import { shareGameResult } from '../services/telegram'
import { getRankColor } from '../utils/helpers'
import confetti from 'canvas-confetti'

export function GameResult() {
  const navigate = useNavigate()
  const { gameId } = useParams()
  const location = useLocation()
  const { user, updateUser } = useUserStore()
  const data = location.state as {
    result: 'win' | 'loss' | 'tie'
    finalScore: { player1: number; player2: number }
    ratingChange: number
    isBotGame?: boolean
    coinsEarned?: number
    mistakes?: Array<{ wordId: number; word: string; correctAnswer: string }>
  }

  const hasShownConfetti = useRef(false)

  useEffect(() => {
    if (data?.result === 'win' && !hasShownConfetti.current) {
      hasShownConfetti.current = true
      confetti({
        particleCount: 100,
        spread: 70,
        origin: { y: 0.6 },
      })
    }

    if (data && user) {
      // Update user state with new rating
      updateUser({
        rating: user.rating + (data.ratingChange || 0),
        coins: user.coins + (data.coinsEarned || 0),
      })
    }
  }, [])

  if (!data || !user) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen gap-4">
        <p>Game result not available</p>
        <Button onClick={() => navigate('/')} fullWidth={false}>Go Home</Button>
      </div>
    )
  }

  const { result, finalScore, ratingChange, isBotGame = false, coinsEarned = 0, mistakes = [] } = data

  const resultConfig = {
    win: { emoji: '🏆', title: 'VICTORY!', color: '#4caf50' },
    loss: { emoji: '💪', title: 'DEFEAT', color: '#f44336' },
    tie: { emoji: '🤝', title: 'DRAW', color: '#ff9800' },
  }

  const cfg = resultConfig[result]

  return (
    <div className="flex flex-col min-h-screen p-4 bg-[var(--tg-theme-bg-color,#fff)]">
      <motion.div
        className="flex flex-col items-center gap-2 py-6"
        initial={{ scale: 0, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ type: 'spring', damping: 10 }}
      >
        <span className="text-6xl">{cfg.emoji}</span>
        <h1 className="text-3xl font-bold" style={{ color: cfg.color }}>
          {cfg.title}
        </h1>
      </motion.div>

      {/* Score */}
      <motion.div
        className="card text-center mb-4"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
      >
        <p className="text-4xl font-bold mb-2">
          {finalScore.player1} - {finalScore.player2}
        </p>
        <div className="flex justify-center items-center gap-3 text-sm">
          <span>Rating: {user.rating}</span>
          <span
            className="font-bold text-lg"
            style={{ color: ratingChange >= 0 ? '#4caf50' : '#f44336' }}
          >
            {ratingChange >= 0 ? '+' : ''}{ratingChange} ⭐
          </span>
        </div>
        <p className="text-sm mt-1">🪙 +{coinsEarned} coins</p>
        {user.streakDays > 0 && (
          <p className="text-sm mt-1">🔥 {user.streakDays} day streak</p>
        )}
      </motion.div>

      {/* Mistakes */}
      {mistakes.length > 0 && (
        <motion.div
          className="mb-4"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
        >
          <p className="font-semibold mb-2">❌ Words to learn:</p>
          <div className="flex flex-col gap-2">
            {mistakes.map((m) => (
              <div
                key={m.wordId}
                className="flex items-center justify-between p-3 bg-red-50 rounded-xl"
              >
                <div>
                  <span className="font-medium">{m.word}</span>
                  <span className="text-[var(--tg-theme-hint-color,#757575)] ml-2">→ {m.correctAnswer}</span>
                </div>
              </div>
            ))}
          </div>
        </motion.div>
      )}

      {/* Actions */}
      <motion.div
        className="mt-auto flex flex-col gap-3"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.5 }}
      >
        <Button onClick={() => navigate(isBotGame ? '/vs-computer' : '/ranked')}>
          PLAY AGAIN
        </Button>
        <Button
          variant="secondary"
          onClick={() => shareGameResult(`${finalScore.player1}-${finalScore.player2}`, user.rating)}
        >
          Share Result 📤
        </Button>
        <button
          className="text-center text-[var(--tg-theme-button-color,#0088cc)] py-2"
          onClick={() => navigate('/')}
        >
          Home
        </button>
      </motion.div>
    </div>
  )
}
