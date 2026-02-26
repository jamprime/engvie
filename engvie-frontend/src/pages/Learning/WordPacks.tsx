import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Header } from '../../components/layout/Header'
import { CategoryCard } from '../../components/learning/CategoryCard'
import { Loader } from '../../components/common/Loader'
import { learningApi } from '../../services/api'
import { WordCategory } from '../../types/word'

export function WordPacks() {
  const navigate = useNavigate()
  const [categories, setCategories] = useState<WordCategory[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    learningApi.getCategories()
      .then((res) => {
        setCategories(res.data.categories || [])
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }, [])

  return (
    <div className="flex flex-col min-h-screen">
      <Header title="🎨 WORD PACKS" showBack />
      <div className="flex-1 p-4">
        {loading ? (
          <Loader />
        ) : (
          <div className="grid grid-cols-2 gap-3">
            {categories.map((cat) => (
              <CategoryCard
                key={cat.id}
                category={cat}
                onClick={() => navigate(`/learning/word-packs/${cat.code}`)}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
