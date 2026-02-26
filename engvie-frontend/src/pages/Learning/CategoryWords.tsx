import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { Header } from '../../components/layout/Header'
import { Loader } from '../../components/common/Loader'
import { learningApi } from '../../services/api'
import { Word, WordCategory } from '../../types/word'
import toast from 'react-hot-toast'

const LEVEL_COLORS: Record<string, { bg: string; text: string }> = {
  a1: { bg: '#e8f5e9', text: '#2e7d32' },
  a2: { bg: '#f1f8e9', text: '#558b2f' },
  b1: { bg: '#fff8e1', text: '#f57f17' },
  b2: { bg: '#fff3e0', text: '#e65100' },
  c1: { bg: '#fce4ec', text: '#c62828' },
  c2: { bg: '#f3e5f5', text: '#6a1b9a' },
}

export function CategoryWords() {
  const { code } = useParams<{ code: string }>()
  const [category, setCategory] = useState<WordCategory | null>(null)
  const [words, setWords] = useState<Word[]>([])
  const [loading, setLoading] = useState(true)
  const [addedIds, setAddedIds] = useState<Set<number>>(new Set())
  const [loadingIds, setLoadingIds] = useState<Set<number>>(new Set())

  useEffect(() => {
    if (!code) return
    learningApi.getCategoryWords(code)
      .then((res) => {
        setCategory(res.data.category)
        setWords(res.data.words || [])
      })
      .catch(() => toast.error('Failed to load words'))
      .finally(() => setLoading(false))
  }, [code])

  const addWord = async (wordId: number) => {
    setLoadingIds((prev) => new Set(prev).add(wordId))
    try {
      await learningApi.addWord(wordId, 'word_pack')
      setAddedIds((prev) => new Set(prev).add(wordId))
      toast.success('Added to My Words')
    } catch (err: any) {
      if (err.response?.status === 409) {
        setAddedIds((prev) => new Set(prev).add(wordId))
        toast('Already in your list')
      } else {
        toast.error('Failed to add word')
      }
    } finally {
      setLoadingIds((prev) => {
        const next = new Set(prev)
        next.delete(wordId)
        return next
      })
    }
  }

  return (
    <div className="flex flex-col min-h-screen">
      <Header
        title={category ? `${category.icon} ${category.nameRu.toUpperCase()}` : 'WORD PACK'}
        showBack
      />

      <div className="flex-1 p-4">
        {loading ? (
          <Loader />
        ) : (
          <>
            {category?.descriptionRu && (
              <p className="text-sm text-[var(--tg-theme-hint-color,#757575)] mb-4">
                {category.descriptionRu}
              </p>
            )}
            <p className="text-sm text-[var(--tg-theme-hint-color,#757575)] mb-3">
              {words.length} words
            </p>
            <div className="flex flex-col gap-3">
              {words.map((word) => {
                const level = LEVEL_COLORS[word.level.toLowerCase()] || LEVEL_COLORS.b1
                const added = addedIds.has(word.id)
                const loading = loadingIds.has(word.id)
                return (
                  <div
                    key={word.id}
                    className="flex items-center justify-between p-4 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)] rounded-2xl"
                  >
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-1">
                        <span className="font-bold text-base">{word.english}</span>
                        <span
                          className="text-xs px-2 py-0.5 rounded-full font-medium uppercase"
                          style={{ backgroundColor: level.bg, color: level.text }}
                        >
                          {word.level}
                        </span>
                      </div>
                      <p className="text-sm text-[var(--tg-theme-hint-color,#757575)]">
                        {word.russian}
                      </p>
                    </div>
                    <button
                      className={`ml-3 px-3 py-1.5 rounded-xl text-sm font-medium transition-all ${
                        added
                          ? 'bg-green-100 text-green-700'
                          : 'bg-[var(--tg-theme-button-color,#0088cc)] text-white active:scale-95'
                      }`}
                      onClick={() => !added && addWord(word.id)}
                      disabled={added || loading}
                    >
                      {loading ? '...' : added ? '✓' : '+'}
                    </button>
                  </div>
                )
              })}
            </div>
          </>
        )}
      </div>
    </div>
  )
}
