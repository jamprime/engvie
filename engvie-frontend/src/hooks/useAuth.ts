import { useCallback, useEffect, useState } from 'react'
import { useUserStore } from '../store'
import { authApi, userApi } from '../services/api'
import { getInitData } from '../services/telegram'

export function useAuth() {
  const { user, token, setUser, setToken, logout } = useUserStore()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const authenticate = useCallback(async () => {
    setLoading(true)
    setError(null)

    try {
      const initData = getInitData()
      // For development without Telegram, use a mock
      const dataToSend = initData || 'mock_init_data'

      const response = await authApi.telegram(dataToSend)
      const { token: newToken, user: newUser } = response.data

      localStorage.setItem('auth_token', newToken)
      setToken(newToken)
      setUser(newUser)
    } catch (err: any) {
      setError(err.response?.data?.error || 'Authentication failed')
    } finally {
      setLoading(false)
    }
  }, [setToken, setUser])

  const refreshUser = useCallback(async () => {
    if (!token) return
    try {
      const response = await userApi.getMe()
      setUser(response.data)
    } catch {
      // Token expired
      logout()
    }
  }, [token, setUser, logout])

  useEffect(() => {
    if (token) {
      // Sync token to localStorage key used by the Axios interceptor
      // (Zustand persist restores the token but doesn't re-run setToken)
      localStorage.setItem('auth_token', token)
      // If user is already set (just authenticated), skip the extra /me call
      if (user) {
        setLoading(false)
        return
      }
      refreshUser().finally(() => setLoading(false))
    } else {
      // token is null: either first visit, logout, or 401 cleared the token.
      // Call authenticate() to get a fresh session.
      authenticate()
    }
  }, [token]) // Re-run when token changes (logout → null, or 401 clears it)

  return { user, token, loading, error, authenticate, refreshUser, logout }
}
