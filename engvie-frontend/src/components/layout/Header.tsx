import React from 'react'
import { useNavigate } from 'react-router-dom'
import { useTelegramBackButton } from '../../hooks/useTelegram'

interface HeaderProps {
  title: string
  showBack?: boolean
  onBack?: () => void
  right?: React.ReactNode
}

export function Header({ title, showBack = false, onBack, right }: HeaderProps) {
  const navigate = useNavigate()
  const handleBack = onBack || (() => navigate(-1))

  useTelegramBackButton(handleBack, showBack)

  return (
    <div className="flex items-center justify-between p-4 sticky top-0 bg-[var(--tg-theme-bg-color,#fff)] z-10">
      {showBack ? (
        <button
          className="p-2 -ml-2 rounded-xl transition-all active:scale-95"
          onClick={handleBack}
        >
          <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
      ) : (
        <div className="w-10" />
      )}
      <h1 className="text-lg font-bold text-center flex-1">{title}</h1>
      {right ? right : <div className="w-10" />}
    </div>
  )
}
