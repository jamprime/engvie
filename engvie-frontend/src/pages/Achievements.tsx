import React, { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { Header } from '../components/layout/Header'
import { Loader } from '../components/common/Loader'
import { achievementsApi } from '../services/api'
import { Achievement } from '../types/api'

export function Achievements() {
  const [achievements, setAchievements] = useState<Achievement[]>([])
  const [totalUnlocked, setTotalUnlocked] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    achievementsApi.getAll().then((res) => {
      setAchievements(res.data.achievements || [])
      setTotalUnlocked(res.data.totalUnlocked || 0)
      setLoading(false)
    })
  }, [])

  const unlocked = achievements.filter((a) => a.unlocked)
  const locked = achievements.filter((a) => !a.unlocked)

  return (
    <div className="flex flex-col min-h-screen">
      <Header title="🏅 ACHIEVEMENTS" showBack />

      {loading ? (
        <Loader />
      ) : (
        <div className="flex-1 p-4">
          <div className="text-center mb-4">
            <p className="text-2xl font-bold">{totalUnlocked}/{achievements.length}</p>
            <p className="text-sm text-[var(--tg-theme-hint-color,#757575)]">achievements unlocked</p>
          </div>

          {unlocked.length > 0 && (
            <div className="mb-4">
              <p className="font-semibold mb-2 text-green-600">Unlocked ✅</p>
              <div className="flex flex-col gap-2">
                {unlocked.map((a, i) => (
                  <AchievementCard key={a.id} achievement={a} index={i} />
                ))}
              </div>
            </div>
          )}

          {locked.length > 0 && (
            <div>
              <p className="font-semibold mb-2 text-[var(--tg-theme-hint-color,#757575)]">Locked 🔒</p>
              <div className="flex flex-col gap-2">
                {locked.map((a, i) => (
                  <AchievementCard key={a.id} achievement={a} index={i} />
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

function AchievementCard({ achievement, index }: { achievement: Achievement; index: number }) {
  return (
    <motion.div
      className={`flex items-center gap-3 p-4 rounded-2xl ${
        achievement.unlocked
          ? 'bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]'
          : 'bg-gray-100 opacity-60'
      }`}
      initial={{ opacity: 0, x: -20 }}
      animate={{ opacity: achievement.unlocked ? 1 : 0.6, x: 0 }}
      transition={{ delay: index * 0.03 }}
    >
      <span className="text-3xl">{achievement.unlocked ? achievement.icon : '🔒'}</span>
      <div className="flex-1">
        <p className="font-semibold text-sm">{achievement.nameRu}</p>
        {achievement.descriptionRu && (
          <p className="text-xs text-[var(--tg-theme-hint-color,#757575)]">{achievement.descriptionRu}</p>
        )}
      </div>
      <div className="text-right">
        <p className="text-sm font-bold">🪙 {achievement.rewardCoins}</p>
        {achievement.unlockedAt && (
          <p className="text-xs text-green-500">Unlocked</p>
        )}
      </div>
    </motion.div>
  )
}
