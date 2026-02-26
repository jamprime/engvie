import React, { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { Header } from '../components/layout/Header'
import { ProgressBar } from '../components/common/ProgressBar'
import { Loader } from '../components/common/Loader'
import { dailyTasksApi } from '../services/api'
import { useUserStore } from '../store'
import { DailyTask } from '../types/api'
import { TASK_TYPE_LABELS } from '../utils/constants'
import { formatTimeUntil } from '../utils/helpers'
import toast from 'react-hot-toast'

export function DailyTasks() {
  const { updateUser } = useUserStore()
  const [tasks, setTasks] = useState<DailyTask[]>([])
  const [loading, setLoading] = useState(true)

  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  tomorrow.setHours(0, 0, 0, 0)

  useEffect(() => {
    dailyTasksApi.getAll().then((res) => {
      setTasks(res.data.tasks || [])
      setLoading(false)
    })
  }, [])

  const claimReward = async (taskId: number) => {
    try {
      const res = await dailyTasksApi.claim(taskId)
      const coins = res.data.coins
      updateUser({ coins: undefined }) // Will be refreshed
      toast.success(`+${coins} coins! 🪙`)
      setTasks((prev) =>
        prev.map((t) => (t.id === taskId ? { ...t, completed: true } : t))
      )
    } catch {
      toast.error('Failed to claim reward')
    }
  }

  return (
    <div className="flex flex-col min-h-screen">
      <Header title="🎯 DAILY TASKS" showBack />
      {loading ? (
        <Loader />
      ) : (
        <div className="flex-1 p-4 flex flex-col gap-3">
          <p className="text-sm text-center text-[var(--tg-theme-hint-color,#757575)]">
            Resets in: {formatTimeUntil(tomorrow.toISOString())}
          </p>

          {tasks.map((task, i) => (
            <motion.div
              key={task.id}
              className="card"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.05 }}
            >
              <div className="flex items-start justify-between mb-2">
                <div>
                  <p className="font-semibold">
                    {TASK_TYPE_LABELS[task.taskType] || task.taskType}
                  </p>
                  <p className="text-sm text-[var(--tg-theme-hint-color,#757575)]">
                    Reward: {task.rewardCoins} coins 🪙
                  </p>
                </div>
                {task.completed && (
                  <span className="text-green-500 text-xl">✅</span>
                )}
              </div>

              <ProgressBar
                value={task.progress}
                max={task.requirement}
                color={task.completed ? '#4caf50' : '#0088cc'}
                showLabel
              />

              {task.progress >= task.requirement && !task.completed && (
                <button
                  className="mt-3 w-full py-2 rounded-xl bg-green-500 text-white font-semibold text-sm active:scale-95 transition-all"
                  onClick={() => claimReward(task.id)}
                >
                  CLAIM {task.rewardCoins} COINS 🪙
                </button>
              )}
            </motion.div>
          ))}
        </div>
      )}
    </div>
  )
}
