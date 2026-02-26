import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { Header } from '../../components/layout/Header'
import { FlipCard } from '../../components/learning/FlipCard'
import { Loader } from '../../components/common/Loader'
import { learningApi } from '../../services/api'
import { UserWord } from '../../types/word'
import toast from 'react-hot-toast'

export function Practice() {
  const navigate = useNavigate()
  const [words, setWords] = useState<UserWord[]>([])
  const [current, setCurrent] = useState(0)
  const [loading, setLoading] = useState(true)
  const [completed, setCompleted] = useState(false)

  useEffect(() => {
    learningApi.getDueWords().then((res) => {
      setWords(res.data.words || [])
      setLoading(false)
    })
  }, [])

  const handleRate = async (quality: 0 | 3 | 4 | 5) => {
    const word = words[current]
    if (!word) return

    try {
      await learningApi.reviewWord(word.word.id, quality)
    } catch {
      toast.error('Failed to save progress')
    }

    if (current + 1 >= words.length) {
      setCompleted(true)
    } else {
      setCurrent((c) => c + 1)
    }
  }

  if (loading) return <Loader fullScreen />

  return (
    <div className="flex flex-col min-h-screen">
      <Header
        title={completed ? 'Practice Complete!' : `${current + 1}/${words.length} cards`}
        showBack
      />

      <div className="flex-1 p-4">
        {words.length === 0 ? (
          <div className="text-center py-10">
            <p className="text-4xl mb-3">🎉</p>
            <p className="text-xl font-bold">All caught up!</p>
            <p className="text-[var(--tg-theme-hint-color,#757575)] mt-2">
              No words due for review today
            </p>
            <button
              className="mt-4 text-[var(--tg-theme-button-color,#0088cc)]"
              onClick={() => navigate('/learning')}
            >
              Back to Learning
            </button>
          </div>
        ) : completed ? (
          <motion.div
            className="text-center py-10"
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
          >
            <p className="text-6xl mb-4">🎊</p>
            <p className="text-2xl font-bold">Session Complete!</p>
            <p className="text-[var(--tg-theme-hint-color,#757575)] mt-2">
              Reviewed {words.length} words
            </p>
            <button
              className="mt-6 btn-primary max-w-xs mx-auto block"
              onClick={() => navigate('/learning')}
            >
              Back to Learning
            </button>
          </motion.div>
        ) : (
          <AnimatePresence mode="wait">
            <motion.div
              key={current}
              initial={{ opacity: 0, x: 50 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -50 }}
            >
              <FlipCard
                front={words[current].word.english}
                back={words[current].word.russian}
                onRate={handleRate}
              />
            </motion.div>
          </AnimatePresence>
        )}
      </div>
    </div>
  )
}
