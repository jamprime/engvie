import React, { useState } from 'react'
import { motion } from 'framer-motion'

interface FlipCardProps {
  front: string
  back: string
  frontFlag?: string
  backFlag?: string
  onRate?: (quality: 0 | 3 | 4 | 5) => void
}

export function FlipCard({ front, back, frontFlag = '🇬🇧', backFlag = '🇷🇺', onRate }: FlipCardProps) {
  const [flipped, setFlipped] = useState(false)

  const handleFlip = () => {
    if (!flipped) setFlipped(true)
  }

  const handleRate = (quality: 0 | 3 | 4 | 5) => {
    setFlipped(false)
    setTimeout(() => onRate?.(quality), 300)
  }

  return (
    <div className="flex flex-col items-center gap-6">
      <div
        className="w-full cursor-pointer"
        onClick={handleFlip}
        style={{ perspective: '1000px' }}
      >
        <motion.div
          className="relative w-full"
          animate={{ rotateY: flipped ? 180 : 0 }}
          transition={{ duration: 0.4, type: 'tween' }}
          style={{ transformStyle: 'preserve-3d' }}
        >
          {/* Front */}
          <div
            className="bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)] rounded-3xl p-8 text-center min-h-[160px] flex flex-col items-center justify-center"
            style={{ backfaceVisibility: 'hidden' }}
          >
            <span className="text-4xl mb-3">{frontFlag}</span>
            <p className="text-2xl font-bold">{front}</p>
            {!flipped && (
              <p className="text-sm text-[var(--tg-theme-hint-color,#757575)] mt-3">
                Tap to reveal
              </p>
            )}
          </div>

          {/* Back */}
          <div
            className="absolute inset-0 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)] rounded-3xl p-8 text-center flex flex-col items-center justify-center"
            style={{ backfaceVisibility: 'hidden', transform: 'rotateY(180deg)' }}
          >
            <span className="text-4xl mb-3">{frontFlag} → {backFlag}</span>
            <p className="text-2xl font-bold">{front}</p>
            <p className="text-3xl my-2">↓</p>
            <p className="text-2xl font-bold text-[var(--tg-theme-button-color,#0088cc)]">{back}</p>
          </div>
        </motion.div>
      </div>

      {flipped && onRate && (
        <motion.div
          className="w-full"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <p className="text-center text-sm text-[var(--tg-theme-hint-color,#757575)] mb-3">
            How well did you know it?
          </p>
          <div className="grid grid-cols-4 gap-2">
            {[
              { q: 0 as const, label: 'Again', color: '#f44336' },
              { q: 3 as const, label: 'Hard', color: '#ff9800' },
              { q: 4 as const, label: 'Good', color: '#0088cc' },
              { q: 5 as const, label: 'Easy', color: '#4caf50' },
            ].map(({ q, label, color }) => (
              <button
                key={q}
                className="py-3 rounded-xl font-medium text-sm text-white transition-all active:scale-95"
                style={{ backgroundColor: color }}
                onClick={() => handleRate(q)}
              >
                {label}
              </button>
            ))}
          </div>
        </motion.div>
      )}
    </div>
  )
}
