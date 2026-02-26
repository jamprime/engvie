import React from 'react'
import { WordCategory } from '../../types/word'

interface CategoryCardProps {
  category: WordCategory
  onClick: () => void
}

export function CategoryCard({ category, onClick }: CategoryCardProps) {
  return (
    <button
      className="flex flex-col items-center justify-center p-4 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)] rounded-2xl gap-2 text-center transition-all active:scale-95 hover:shadow-md"
      onClick={onClick}
    >
      <span className="text-3xl">{category.icon}</span>
      <span className="font-medium text-sm">{category.nameRu}</span>
      <span className="text-xs text-[var(--tg-theme-hint-color,#757575)]">
        {category.wordsCount} words
      </span>
    </button>
  )
}
