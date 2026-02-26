import React from 'react'

interface ProgressBarProps {
  value: number
  max: number
  color?: string
  height?: string
  showLabel?: boolean
}

export function ProgressBar({ value, max, color = '#0088cc', height = '8px', showLabel = false }: ProgressBarProps) {
  const percent = Math.min(100, Math.round((value / max) * 100))

  return (
    <div className="w-full">
      {showLabel && (
        <div className="flex justify-between text-xs text-[var(--tg-theme-hint-color,#757575)] mb-1">
          <span>{value}/{max}</span>
          <span>{percent}%</span>
        </div>
      )}
      <div className="w-full bg-gray-200 rounded-full overflow-hidden" style={{ height }}>
        <div
          className="h-full rounded-full transition-all duration-300"
          style={{ width: `${percent}%`, backgroundColor: color }}
        />
      </div>
    </div>
  )
}
