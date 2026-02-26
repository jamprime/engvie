import React, { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Header } from '../components/layout/Header'
import { Button } from '../components/common/Button'
import { useUserStore } from '../store'
import { matchmakingApi, userApi } from '../services/api'

const ENERGY_COST = 60
import { wsService } from '../services/websocket'
import toast from 'react-hot-toast'

type Phase = 'select' | 'searching' | 'found'

export function Ranked() {
  const navigate = useNavigate()
  const { user, setUser } = useUserStore()
  const [phase, setPhase] = useState<Phase>('select')
  const [buyingEnergy, setBuyingEnergy] = useState(false)
  const [rounds, setRounds] = useState(3)
  const [searchTime, setSearchTime] = useState(0)
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null)

  useEffect(() => {
    // Refresh energy and user data on mount
    userApi.getMe().then((res) => setUser(res.data)).catch(() => {})

    // Subscribe to matchmaking events
    wsService.subscribe('/user/queue/matchmaking', (data) => {
      if (data.type === 'MATCHMAKING_FOUND') {
        setPhase('found')
        clearTimerAndNavigate(data.gameId)
      }
    })

    return () => {
      wsService.unsubscribe('/user/queue/matchmaking')
      if (timerRef.current) clearInterval(timerRef.current)
    }
  }, [])

  const clearTimerAndNavigate = (gameId: number) => {
    if (timerRef.current) clearInterval(timerRef.current)
    setTimeout(() => navigate(`/battle/${gameId}`), 1000)
  }

  const startSearch = async () => {
    if (!user) return
    if (user.energy < 1) {
      toast.error('Not enough energy! ⚡')
      return
    }

    try {
      await matchmakingApi.join(rounds)
      // Optimistic update: deduct energy immediately in store
      const nextEnergyInSeconds = user.nextEnergyInSeconds ?? 30 * 60
      setUser({ ...user, energy: user.energy - 1, nextEnergyInSeconds })
      setPhase('searching')
      setSearchTime(0)

      timerRef.current = setInterval(() => {
        setSearchTime((t) => {
          if (t >= 10) {
            clearInterval(timerRef.current!)
            // Backend will assign bot, we'll get MATCHMAKING_FOUND via WS
          }
          return t + 1
        })
      }, 1000)
    } catch (err: any) {
      toast.error(err.response?.data?.error || 'Failed to join queue')
    }
  }

  const handleBuyEnergy = async () => {
    if (!user || buyingEnergy) return
    if (user.coins < ENERGY_COST) {
      toast.error(`Not enough coins! Need ${ENERGY_COST}🪙`)
      return
    }
    setBuyingEnergy(true)
    try {
      const res = await userApi.buyEnergy()
      setUser(res.data)
      toast.success('⚡ +1 energy purchased!')
    } catch (err: any) {
      toast.error(err.response?.data?.error || 'Purchase failed')
    } finally {
      setBuyingEnergy(false)
    }
  }

  const cancelSearch = async () => {
    try {
      await matchmakingApi.cancel()
    } catch {}
    if (timerRef.current) clearInterval(timerRef.current)
    setPhase('select')
    setSearchTime(0)
  }

  if (!user) return null

  return (
    <div className="flex flex-col min-h-screen">
      <Header title="🏆 RANKED" showBack={phase === 'select'} />

      <div className="flex-1 flex flex-col items-center justify-center p-6">
        {phase === 'select' && (
          <motion.div
            className="w-full flex flex-col gap-6"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <div className="card">
              <p className="text-center text-[var(--tg-theme-hint-color,#757575)] mb-4">Your rating</p>
              <p className="text-4xl font-bold text-center">{user.rating} ⭐</p>
              <p className="text-center text-sm mt-2">
                Looking for: {user.rating - 200} - {user.rating + 200}
              </p>
            </div>

            <div>
              <p className="font-semibold mb-3">How many rounds?</p>
              <div className="flex gap-3">
                {[3, 5].map((r) => (
                  <button
                    key={r}
                    className={`flex-1 py-4 rounded-2xl font-bold text-lg border-2 transition-all ${
                      rounds === r
                        ? 'border-[var(--tg-theme-button-color,#0088cc)] bg-[var(--tg-theme-button-color,#0088cc)] text-white'
                        : 'border-gray-200 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]'
                    }`}
                    onClick={() => setRounds(r)}
                  >
                    {r} rounds
                    <span className="block text-xs font-normal opacity-70">
                      {r === 3 ? '🔥 Quick' : 'Standard'}
                    </span>
                  </button>
                ))}
              </div>
            </div>

            <div className="flex items-center justify-between">
              <span className="text-sm text-[var(--tg-theme-hint-color,#757575)]">
                ⚡ Cost: 1 energy ({user.energy}/{user.maxEnergy} available)
              </span>
              {user.energy < user.maxEnergy && (
                <button
                  onClick={handleBuyEnergy}
                  disabled={buyingEnergy || user.coins < ENERGY_COST}
                  className="text-sm font-semibold px-3 py-1.5 rounded-xl transition-all active:scale-95 disabled:opacity-50"
                  style={{ backgroundColor: 'var(--tg-theme-button-color,#0088cc)', color: '#fff' }}
                >
                  {buyingEnergy ? '...' : `+⚡ ${ENERGY_COST}🪙`}
                </button>
              )}
            </div>

            <Button onClick={startSearch} disabled={user.energy < 1}>
              {user.energy < 1 ? 'No energy ⚡' : 'PLAY'}
            </Button>
          </motion.div>
        )}

        {phase === 'searching' && (
          <motion.div
            className="w-full flex flex-col items-center gap-6"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
          >
            <div className="flex flex-col items-center gap-4">
              <motion.div
                className="w-24 h-24 rounded-full border-4 border-[var(--tg-theme-button-color,#0088cc)] flex items-center justify-center text-4xl"
                animate={{ scale: [1, 1.1, 1] }}
                transition={{ duration: 1.5, repeat: Infinity }}
              >
                🔍
              </motion.div>
              <p className="text-xl font-bold">Searching...</p>
              <p className="text-[var(--tg-theme-hint-color,#757575)]">⏱️ {searchTime} seconds</p>
              {searchTime >= 8 && (
                <p className="text-sm text-[var(--tg-theme-hint-color,#757575)]">
                  Connecting to bot...
                </p>
              )}
            </div>
            <Button variant="secondary" onClick={cancelSearch}>
              Cancel Search
            </Button>
          </motion.div>
        )}

        {phase === 'found' && (
          <motion.div
            className="w-full flex flex-col items-center gap-4"
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
          >
            <span className="text-6xl">🎉</span>
            <p className="text-2xl font-bold">Opponent found!</p>
            <p className="text-[var(--tg-theme-hint-color,#757575)]">Starting game...</p>
          </motion.div>
        )}
      </div>
    </div>
  )
}
