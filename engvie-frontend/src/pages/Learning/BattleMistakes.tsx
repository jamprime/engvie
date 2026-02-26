import React, { useEffect, useState } from 'react'
import { Header } from '../../components/layout/Header'
import { WordCard } from '../../components/learning/WordCard'
import { Loader } from '../../components/common/Loader'
import { learningApi } from '../../services/api'
import { UserWord } from '../../types/word'

export function BattleMistakes() {
  const [words, setWords] = useState<UserWord[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    learningApi.getMistakes().then((res) => {
      setWords(res.data.words || [])
      setLoading(false)
    })
  }, [])

  return (
    <div className="flex flex-col min-h-screen">
      <Header title="❌ BATTLE MISTAKES" showBack />
      <div className="flex-1 p-4">
        {loading ? (
          <Loader />
        ) : words.length === 0 ? (
          <div className="text-center py-10 text-[var(--tg-theme-hint-color,#757575)]">
            <p className="text-4xl mb-3">🎯</p>
            <p>No mistakes yet!</p>
            <p className="text-sm mt-1">Words from battles will appear here</p>
          </div>
        ) : (
          <div className="flex flex-col gap-3">
            <p className="text-sm text-[var(--tg-theme-hint-color,#757575)]">
              {words.length} words from battles
            </p>
            {words.map((uw) => (
              <WordCard key={uw.id} userWord={uw} />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
