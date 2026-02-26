import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import { Header } from '../../components/layout/Header'
import { learningApi } from '../../services/api'

export function Learning() {
  const navigate = useNavigate()
  const [dueCount, setDueCount] = useState(0)
  const [wordsCount, setWordsCount] = useState(0)
  const [mistakesCount, setMistakesCount] = useState(0)

  useEffect(() => {
    learningApi.getDueWords().then((r) => setDueCount(r.data.words?.length || 0))
    learningApi.getMyWords().then((r) => setWordsCount(r.data.total || 0))
    learningApi.getMistakes().then((r) => setMistakesCount(r.data.words?.length || 0))
  }, [])

  const items = [
    {
      icon: '📖',
      title: 'MY WORDS',
      desc: `${wordsCount} words`,
      path: '/learning/my-words',
    },
    {
      icon: '🎯',
      title: 'PRACTICE',
      desc: dueCount > 0 ? `${dueCount} words due today` : 'All caught up!',
      path: '/learning/practice',
    },
    {
      icon: '🎨',
      title: 'WORD PACKS',
      desc: 'Thematic collections',
      path: '/learning/word-packs',
    },
    {
      icon: '❌',
      title: 'BATTLE MISTAKES',
      desc: mistakesCount > 0 ? `${mistakesCount} words to review` : 'No mistakes',
      path: '/learning/mistakes',
    },
  ]

  return (
    <div className="flex flex-col min-h-screen">
      <Header title="📚 LEARNING" showBack />
      <div className="flex-1 p-4 flex flex-col gap-3">
        {items.map((item, i) => (
          <motion.button
            key={item.path}
            className="w-full p-5 rounded-2xl text-left flex items-center gap-4 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]"
            onClick={() => navigate(item.path)}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: i * 0.05 }}
            whileTap={{ scale: 0.97 }}
          >
            <span className="text-3xl">{item.icon}</span>
            <div>
              <p className="font-bold">{item.title}</p>
              <p className="text-sm text-[var(--tg-theme-hint-color,#757575)]">{item.desc}</p>
            </div>
            <span className="ml-auto text-[var(--tg-theme-hint-color,#757575)]">›</span>
          </motion.button>
        ))}
      </div>
    </div>
  )
}
