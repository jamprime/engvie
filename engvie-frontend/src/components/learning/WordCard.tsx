import React from 'react'
import { UserWord } from '../../types/word'

interface WordCardProps {
  userWord: UserWord
  onAddToStudy?: () => void
  onRemove?: () => void
}

const STATUS_COLORS = {
  learning: { bg: '#fff3e0', text: '#e65100', label: 'Learning' },
  learned: { bg: '#e8f5e9', text: '#2e7d32', label: 'Learned' },
  mastered: { bg: '#e3f2fd', text: '#1565c0', label: 'Mastered' },
}

export function WordCard({ userWord, onRemove }: WordCardProps) {
  const status = STATUS_COLORS[userWord.status as keyof typeof STATUS_COLORS] || STATUS_COLORS.learning

  return (
    <div className="flex items-center justify-between p-4 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)] rounded-2xl">
      <div className="flex-1">
        <div className="flex items-center gap-2 mb-1">
          <span className="font-bold text-base">{userWord.word.english}</span>
          <span
            className="text-xs px-2 py-0.5 rounded-full font-medium"
            style={{ backgroundColor: status.bg, color: status.text }}
          >
            {status.label}
          </span>
        </div>
        <p className="text-sm text-[var(--tg-theme-hint-color,#757575)]">{userWord.word.russian}</p>
        <div className="flex items-center gap-3 mt-1 text-xs text-[var(--tg-theme-hint-color,#757575)]">
          <span>✅ {userWord.correctCount}</span>
          <span>❌ {userWord.incorrectCount}</span>
          <span className="uppercase">{userWord.word.level}</span>
        </div>
      </div>
      {onRemove && (
        <button
          className="p-2 text-red-400 hover:text-red-600 transition-colors"
          onClick={onRemove}
        >
          🗑️
        </button>
      )}
    </div>
  )
}
