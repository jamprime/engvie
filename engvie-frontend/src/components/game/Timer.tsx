import React, { useEffect, useRef, useState } from 'react'
import { formatTimerColor } from '../../utils/helpers'

interface TimerProps {
  totalMs: number
  onExpire?: () => void
  paused?: boolean
}

export function Timer({ totalMs, onExpire, paused = false }: TimerProps) {
  const [timeLeft, setTimeLeft] = useState(totalMs)
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null)
  const expiredRef = useRef(false)

  useEffect(() => {
    setTimeLeft(totalMs)
    expiredRef.current = false
  }, [totalMs])

  useEffect(() => {
    if (paused) {
      if (intervalRef.current) clearInterval(intervalRef.current)
      return
    }

    intervalRef.current = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 100) {
          clearInterval(intervalRef.current!)
          if (!expiredRef.current) {
            expiredRef.current = true
            onExpire?.()
          }
          return 0
        }
        return prev - 100
      })
    }, 100)

    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current)
    }
  }, [paused, onExpire])

  const seconds = Math.ceil(timeLeft / 1000)
  const percent = timeLeft / totalMs
  const color = formatTimerColor(timeLeft, totalMs)
  const isWarning = percent < 0.3

  return (
    <div className="w-full">
      <div className="flex items-center justify-between mb-1">
        <span
          className={`text-2xl font-bold tabular-nums ${isWarning ? 'animate-pulse' : ''}`}
          style={{ color }}
        >
          {seconds}s
        </span>
      </div>
      <div className="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
        <div
          className="h-full rounded-full transition-none"
          style={{ width: `${percent * 100}%`, backgroundColor: color }}
        />
      </div>
    </div>
  )
}
