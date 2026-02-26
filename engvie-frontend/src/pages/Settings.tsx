import React from 'react'
import { useNavigate } from 'react-router-dom'
import { Header } from '../components/layout/Header'
import { useUserStore } from '../store'
import { LEVEL_LABELS } from '../utils/constants'
import toast from 'react-hot-toast'

export function Settings() {
  const navigate = useNavigate()
  const { user, logout } = useUserStore()

  const handleDeleteAccount = () => {
    if (window.confirm('Are you sure you want to delete your account? This cannot be undone.')) {
      logout()
      navigate('/')
    }
  }

  if (!user) return null

  return (
    <div className="flex flex-col min-h-screen">
      <Header title="⚙️ SETTINGS" showBack />
      <div className="flex-1 p-4 flex flex-col gap-4">
        {/* Account */}
        <div className="card">
          <p className="text-xs font-semibold text-[var(--tg-theme-hint-color,#757575)] uppercase mb-3">
            Account
          </p>
          <div className="flex flex-col gap-2">
            <div className="flex justify-between text-sm">
              <span>Name</span>
              <span className="font-medium">{user.firstName} {user.lastName || ''}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span>Username</span>
              <span className="font-medium">@{user.username}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span>Level</span>
              <span className="font-medium">{user.level} — {LEVEL_LABELS[user.level] || user.level}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span>Rating</span>
              <span className="font-medium">{user.rating} ⭐</span>
            </div>
          </div>
        </div>

        {/* Stats */}
        <div className="card">
          <p className="text-xs font-semibold text-[var(--tg-theme-hint-color,#757575)] uppercase mb-3">
            Statistics
          </p>
          <div className="grid grid-cols-3 gap-2 text-center">
            <div>
              <p className="text-2xl font-bold text-green-500">{user.wins}</p>
              <p className="text-xs text-[var(--tg-theme-hint-color,#757575)]">Wins</p>
            </div>
            <div>
              <p className="text-2xl font-bold text-red-500">{user.losses}</p>
              <p className="text-xs text-[var(--tg-theme-hint-color,#757575)]">Losses</p>
            </div>
            <div>
              <p className="text-2xl font-bold text-orange-500">{user.draws}</p>
              <p className="text-xs text-[var(--tg-theme-hint-color,#757575)]">Draws</p>
            </div>
          </div>
        </div>

        {/* About */}
        <div className="card">
          <p className="text-xs font-semibold text-[var(--tg-theme-hint-color,#757575)] uppercase mb-3">
            About
          </p>
          <div className="flex flex-col gap-2 text-sm">
            <button
              className="text-left py-1 text-[var(--tg-theme-button-color,#0088cc)]"
              onClick={() => toast('Privacy Policy — coming soon')}
            >
              Privacy Policy
            </button>
            <button
              className="text-left py-1 text-[var(--tg-theme-button-color,#0088cc)]"
              onClick={() => toast('Terms of Service — coming soon')}
            >
              Terms of Service
            </button>
            <p className="text-[var(--tg-theme-hint-color,#757575)]">Version 1.0.0</p>
          </div>
        </div>

        <div className="mt-auto">
          <button
            className="w-full py-3 rounded-xl bg-red-50 text-red-500 font-medium text-sm border border-red-200 transition-all active:scale-95"
            onClick={handleDeleteAccount}
          >
            🗑️ Delete Account
          </button>
        </div>
      </div>
    </div>
  )
}
