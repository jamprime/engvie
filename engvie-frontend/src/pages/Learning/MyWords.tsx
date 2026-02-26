import React, { useEffect, useState } from 'react'
import { Header } from '../../components/layout/Header'
import { WordCard } from '../../components/learning/WordCard'
import { Loader } from '../../components/common/Loader'
import { learningApi } from '../../services/api'
import { UserWord } from '../../types/word'
import toast from 'react-hot-toast'

const STATUS_FILTERS = [
  { value: undefined, label: 'All' },
  { value: 'learning', label: 'Learning' },
  { value: 'learned', label: 'Learned' },
  { value: 'mastered', label: 'Mastered' },
]

export function MyWords() {
  const [words, setWords] = useState<UserWord[]>([])
  const [loading, setLoading] = useState(true)
  const [statusFilter, setStatusFilter] = useState<string | undefined>()

  const loadWords = async () => {
    setLoading(true)
    try {
      const res = await learningApi.getMyWords(statusFilter)
      setWords(res.data.words || [])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { loadWords() }, [statusFilter])

  const removeWord = async (wordId: number) => {
    try {
      await learningApi.removeWord(wordId)
      setWords((prev) => prev.filter((w) => w.word.id !== wordId))
      toast.success('Word removed')
    } catch {
      toast.error('Failed to remove word')
    }
  }

  return (
    <div className="flex flex-col min-h-screen">
      <Header title="📖 MY WORDS" showBack />

      {/* Filters */}
      <div className="flex gap-2 px-4 py-2 overflow-x-auto">
        {STATUS_FILTERS.map((f) => (
          <button
            key={String(f.value)}
            className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-all ${
              statusFilter === f.value
                ? 'bg-[var(--tg-theme-button-color,#0088cc)] text-white'
                : 'bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]'
            }`}
            onClick={() => setStatusFilter(f.value)}
          >
            {f.label}
          </button>
        ))}
      </div>

      <div className="flex-1 p-4">
        {loading ? (
          <Loader />
        ) : words.length === 0 ? (
          <div className="text-center py-10 text-[var(--tg-theme-hint-color,#757575)]">
            <p className="text-4xl mb-3">📭</p>
            <p>No words yet</p>
            <p className="text-sm mt-1">Add words from Word Packs or battles</p>
          </div>
        ) : (
          <div className="flex flex-col gap-3">
            <p className="text-sm text-[var(--tg-theme-hint-color,#757575)]">
              {words.length} words
            </p>
            {words.map((uw) => (
              <WordCard
                key={uw.id}
                userWord={uw}
                onRemove={() => removeWord(uw.word.id)}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
