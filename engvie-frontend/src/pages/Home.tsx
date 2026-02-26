import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { useUserStore } from '../store'
import { userApi } from '../services/api'
import { getRankColor } from '../utils/helpers'

function useEnergyCountdown(initialSeconds: number | null | undefined, onExpire?: () => void) {
  const [seconds, setSeconds] = useState<number>(initialSeconds ?? 0)

  useEffect(() => {
    if (!initialSeconds || initialSeconds <= 0) { setSeconds(0); return }
    setSeconds(initialSeconds)
    const id = setInterval(() => {
      setSeconds((s) => {
        if (s <= 1) { clearInterval(id); onExpire?.(); return 0 }
        return s - 1
      })
    }, 1000)
    return () => clearInterval(id)
  }, [initialSeconds])

  if (!seconds || seconds <= 0) return null
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${String(s).padStart(2, '0')}`
}

export function Home() {
  const navigate = useNavigate()
  const { user, setUser } = useUserStore()

  const refreshUser = () => userApi.getMe().then((r) => setUser(r.data)).catch(() => {})

  // Always refresh on mount: gets fresh energy + nextEnergyAt (not in old Zustand cache)
  useEffect(() => { refreshUser() }, [])

  const countdown = useEnergyCountdown(user?.nextEnergyInSeconds, refreshUser)

  if (!user) return null

  const energyBars = Array.from({ length: user.maxEnergy }, (_, i) => i < user.energy)

  return (
    <div className="flex flex-col min-h-screen bg-[var(--tg-theme-bg-color,#fff)] pb-safe-bottom">
      {/* Header */}
      <div className="p-4 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-full bg-gray-300 flex items-center justify-center text-xl font-bold">
              {user.firstName[0]}
            </div>
            <div>
              <p className="font-bold text-base">{user.firstName}</p>
              <p className="text-sm" style={{ color: getRankColor(user.rank) }}>
                {user.rank} · {user.rating} ⭐
              </p>
            </div>
          </div>
          <button
            onClick={() => navigate('/settings')}
            className="p-2 rounded-xl bg-[var(--tg-theme-bg-color,#fff)]"
          >
            ⚙️
          </button>
        </div>

        <div className="flex items-center justify-between">
          {/* Energy */}
          <div className="flex items-center gap-2">
            <span className="text-sm font-medium">⚡</span>
            <div className="flex gap-1">
              {energyBars.map((full, i) => (
                <div
                  key={i}
                  className="w-5 h-5 rounded-full border-2"
                  style={{
                    backgroundColor: full ? '#0088cc' : 'transparent',
                    borderColor: '#0088cc',
                  }}
                />
              ))}
            </div>
            <span className="text-sm text-[var(--tg-theme-hint-color,#757575)]">
              {user.energy}/{user.maxEnergy}
            </span>
            {countdown && (
              <span className="text-xs text-[var(--tg-theme-hint-color,#757575)]">
                {countdown}
              </span>
            )}
          </div>
          {/* Coins */}
          <div className="flex items-center gap-1">
            <span>🪙</span>
            <span className="font-bold">{user.coins}</span>
          </div>
        </div>

        {user.streakDays > 0 && (
          <div className="mt-2 flex items-center gap-1 text-sm">
            <span>🔥</span>
            <span className="font-medium">{user.streakDays} day streak</span>
          </div>
        )}
      </div>

      {/* Main Buttons */}
      <div className="flex-1 p-4 flex flex-col gap-3">
        <motion.button
          className="w-full p-5 rounded-2xl text-left flex items-center gap-4 transition-all"
          style={{ backgroundColor: '#0088cc' }}
          onClick={() => navigate('/ranked')}
          whileTap={{ scale: 0.97 }}
        >
          <span className="text-4xl">🏆</span>
          <div>
            <p className="text-xl font-bold text-white">RANKED</p>
            <p className="text-white/80 text-sm">Find an opponent</p>
            <p className="text-white/60 text-xs mt-1">⚡ 1 energy</p>
          </div>
        </motion.button>

        <motion.button
          className="w-full p-5 rounded-2xl text-left flex items-center gap-4 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]"
          onClick={() => navigate('/vs-computer')}
          whileTap={{ scale: 0.97 }}
        >
          <span className="text-4xl">🤖</span>
          <div>
            <p className="text-xl font-bold">VS COMPUTER</p>
            <p className="text-[var(--tg-theme-hint-color,#757575)] text-sm">Practice mode</p>
            <p className="text-[var(--tg-theme-hint-color,#757575)] text-xs mt-1">⚡ Free</p>
          </div>
        </motion.button>

        <motion.button
          className="w-full p-5 rounded-2xl text-left flex items-center gap-4 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]"
          onClick={() => navigate('/learning')}
          whileTap={{ scale: 0.97 }}
        >
          <span className="text-4xl">📚</span>
          <div>
            <p className="text-xl font-bold">LEARNING</p>
            <p className="text-[var(--tg-theme-hint-color,#757575)] text-sm">Improve your skills</p>
          </div>
        </motion.button>
      </div>

      {/* Bottom Navigation */}
      <div className="grid grid-cols-4 gap-1 p-3 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]">
        {[
          { icon: '📊', label: 'Stats', path: '/settings' },
          { icon: '🏅', label: 'Badges', path: '/achievements' },
          { icon: '🎯', label: 'Tasks', path: '/daily-tasks' },
          { icon: '📈', label: 'Leaders', path: '/leaderboard' },
        ].map(({ icon, label, path }) => (
          <button
            key={path}
            className="flex flex-col items-center py-2 px-1 rounded-xl transition-all active:scale-95"
            onClick={() => navigate(path)}
          >
            <span className="text-xl">{icon}</span>
            <span className="text-xs mt-1 text-[var(--tg-theme-hint-color,#757575)]">{label}</span>
          </button>
        ))}
      </div>
    </div>
  )
}
