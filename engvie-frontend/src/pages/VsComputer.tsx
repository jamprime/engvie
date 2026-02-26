import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Header } from '../components/layout/Header'
import { Button } from '../components/common/Button'
import { gamesApi } from '../services/api'
import { Difficulty } from '../types/game'
import toast from 'react-hot-toast'

const DIFFICULTIES = [
  { value: 'easy' as Difficulty, label: '😊 EASY', desc: 'For beginners', accuracy: '70%', coins: { win: 5, draw: 2, loss: 1 } },
  { value: 'medium' as Difficulty, label: '😐 MEDIUM', desc: 'For intermediate', accuracy: '85%', coins: { win: 12, draw: 5, loss: 2 } },
  { value: 'hard' as Difficulty, label: '😈 HARD', desc: 'For advanced', accuracy: '95%', coins: { win: 20, draw: 8, loss: 3 } },
]

export function VsComputer() {
  const navigate = useNavigate()
  const [difficulty, setDifficulty] = useState<Difficulty>('medium')
  const [rounds, setRounds] = useState(3)
  const [loading, setLoading] = useState(false)

  const startGame = async () => {
    setLoading(true)
    try {
      const response = await gamesApi.startComputerGame(difficulty, rounds)
      navigate(`/battle/${response.data.gameId}`)
    } catch (err: any) {
      toast.error('Failed to start game')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex flex-col min-h-screen">
      <Header title="🤖 VS COMPUTER" showBack />
      <div className="flex-1 p-4 flex flex-col gap-4">
        <p className="font-semibold">Choose difficulty:</p>

        <div className="flex flex-col gap-3">
          {DIFFICULTIES.map((d) => (
            <motion.button
              key={d.value}
              className={`p-4 rounded-2xl text-left border-2 transition-all flex items-center gap-4 ${
                difficulty === d.value
                  ? 'border-[var(--tg-theme-button-color,#0088cc)] bg-blue-50'
                  : 'border-transparent bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]'
              }`}
              onClick={() => setDifficulty(d.value)}
              whileTap={{ scale: 0.97 }}
            >
              <span className="text-3xl">{d.label.split(' ')[0]}</span>
              <div className="flex-1">
                <p className="font-bold">{d.label.split(' ').slice(1).join(' ')}</p>
                <p className="text-sm text-[var(--tg-theme-hint-color,#757575)]">{d.desc} · Bot accuracy: {d.accuracy}</p>
                <p className="text-xs mt-1">
                  🪙 Win <span className="font-semibold text-green-600">+{d.coins.win}</span>
                  {' · '}Draw <span className="font-semibold text-orange-500">+{d.coins.draw}</span>
                  {' · '}Loss <span className="font-semibold text-red-500">+{d.coins.loss}</span>
                </p>
              </div>
            </motion.button>
          ))}
        </div>

        <div>
          <p className="font-semibold mb-3">Rounds:</p>
          <div className="flex gap-3">
            {[3, 5].map((r) => (
              <button
                key={r}
                className={`flex-1 py-3 rounded-2xl font-bold border-2 transition-all ${
                  rounds === r
                    ? 'border-[var(--tg-theme-button-color,#0088cc)] bg-[var(--tg-theme-button-color,#0088cc)] text-white'
                    : 'border-gray-200 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]'
                }`}
                onClick={() => setRounds(r)}
              >
                {r}
              </button>
            ))}
          </div>
        </div>

        <div className="text-sm text-[var(--tg-theme-hint-color,#757575)]">
          <p>⚡ No energy cost · 📊 No rating change</p>
        </div>

        <div className="mt-auto">
          <Button onClick={startGame} loading={loading}>
            START GAME
          </Button>
        </div>
      </div>
    </div>
  )
}
