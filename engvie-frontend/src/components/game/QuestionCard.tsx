import React, { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { hapticFeedback } from '../../services/telegram'
import { classNames } from '../../utils/helpers'

interface QuestionCardProps {
  word: string
  direction: 'en_to_ru' | 'ru_to_en'
  options: string[]
  onAnswer: (answer: string) => void
  correctAnswer?: string
  selectedAnswer?: string
  disabled?: boolean
}

export function QuestionCard({
  word,
  direction,
  options,
  onAnswer,
  correctAnswer,
  selectedAnswer,
  disabled = false,
}: QuestionCardProps) {
  const flag = direction === 'en_to_ru' ? '🇬🇧' : '🇷🇺'
  const prompt = direction === 'en_to_ru' ? 'Translate to Russian:' : 'Translate to English:'

  const handleAnswer = (option: string) => {
    if (disabled || selectedAnswer) return
    hapticFeedback('medium')
    onAnswer(option)
  }

  const getOptionStyle = (option: string) => {
    if (!selectedAnswer) return ''
    if (!correctAnswer) return ''  // answer sent, waiting for server result
    if (option === correctAnswer) return 'correct'
    if (option === selectedAnswer && selectedAnswer !== correctAnswer) return 'incorrect'
    return 'opacity-50'
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="text-center">
        <p className="text-sm text-[var(--tg-theme-hint-color,#757575)] mb-2">{prompt}</p>
        <div className="bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)] rounded-2xl p-6">
          <span className="text-3xl mr-2">{flag}</span>
          <span className="text-3xl font-bold">{word}</span>
        </div>
      </div>

      <div className="flex flex-col gap-3">
        {options.map((option, i) => (
          <motion.button
            key={option}
            className={classNames('answer-option', getOptionStyle(option))}
            onClick={() => handleAnswer(option)}
            disabled={disabled || !!selectedAnswer}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: i * 0.05 }}
            whileTap={{ scale: 0.97 }}
          >
            <span className="text-[var(--tg-theme-hint-color,#757575)] mr-2 font-bold">
              {String.fromCharCode(65 + i)})
            </span>
            {option}
            {selectedAnswer && correctAnswer && option === correctAnswer && (
              <span className="ml-auto text-green-500">✅</span>
            )}
            {selectedAnswer && correctAnswer && selectedAnswer === option && option !== correctAnswer && (
              <span className="ml-auto text-red-500">❌</span>
            )}
          </motion.button>
        ))}
      </div>
    </div>
  )
}
