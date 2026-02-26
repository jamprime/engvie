import React, { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { Header } from '../components/layout/Header'
import { Loader } from '../components/common/Loader'
import { leaderboardApi } from '../services/api'
import { useUserStore } from '../store'
import { LeaderboardEntry } from '../types/api'
import { getRankColor } from '../utils/helpers'

const POSITION_MEDALS: Record<number, string> = { 1: '👑', 2: '🥈', 3: '🥉' }

export function Leaderboard() {
  const { user } = useUserStore()
  const [entries, setEntries] = useState<LeaderboardEntry[]>([])
  const [myPosition, setMyPosition] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    leaderboardApi.getGlobal().then((res) => {
      setEntries(res.data.leaderboard || [])
      setMyPosition(res.data.myPosition || 0)
      setLoading(false)
    })
  }, [])

  return (
    <div className="flex flex-col min-h-screen">
      <Header title="📈 LEADERBOARD" showBack />

      {loading ? (
        <Loader />
      ) : (
        <div className="flex-1 overflow-y-auto">
          {/* My position */}
          {myPosition > 0 && (
            <div className="px-4 py-2 bg-blue-50 sticky top-0">
              <p className="text-sm text-center">
                Your position: <strong>#{myPosition}</strong>
              </p>
            </div>
          )}

          <div className="p-2">
            {entries.map((entry, index) => (
              <motion.div
                key={entry.userId}
                className={`flex items-center gap-3 p-3 rounded-xl mb-1 ${
                  entry.userId === user?.id
                    ? 'bg-blue-100 border-2 border-[var(--tg-theme-button-color,#0088cc)]'
                    : 'hover:bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]'
                }`}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.01 }}
              >
                <span className="w-8 text-center text-lg font-bold">
                  {POSITION_MEDALS[entry.rank] || entry.rank}
                </span>
                <div className="w-9 h-9 rounded-full bg-gray-300 flex items-center justify-center font-bold text-sm">
                  {entry.username[0].toUpperCase()}
                </div>
                <div className="flex-1">
                  <p className="font-semibold text-sm">
                    {entry.username}
                    {entry.userId === user?.id && (
                      <span className="text-xs text-blue-500 ml-1">(you)</span>
                    )}
                  </p>
                  <p className="text-xs" style={{ color: getRankColor(entry.rankTitle) }}>
                    {entry.rankTitle}
                  </p>
                </div>
                <div className="text-right">
                  <p className="font-bold text-sm">{entry.rating} ⭐</p>
                  <p className="text-xs text-[var(--tg-theme-hint-color,#757575)]">
                    {entry.wins}W
                  </p>
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
