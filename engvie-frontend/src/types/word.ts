export interface Word {
  id: number
  english: string
  russian: string
  level: string
  category?: string
}

export interface UserWord {
  id: number
  word: Word
  status: 'learning' | 'learned' | 'mastered'
  correctCount: number
  incorrectCount: number
  lastReviewed?: string
  nextReview: string
  easeFactor: number
  intervalDays: number
  addedFrom: string
}

export interface WordCategory {
  id: number
  code: string
  nameEn: string
  nameRu: string
  descriptionRu?: string
  icon: string
  wordsCount: number
  minLevel: string
}
