import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { User } from '../types/user'

interface UserStore {
  user: User | null
  token: string | null
  setUser: (user: User) => void
  setToken: (token: string) => void
  updateUser: (updates: Partial<User>) => void
  logout: () => void
}

export const useUserStore = create<UserStore>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      setUser: (user) => set({ user }),
      setToken: (token) => {
        localStorage.setItem('auth_token', token)
        set({ token })
      },
      updateUser: (updates) =>
        set((state) => ({
          user: state.user ? { ...state.user, ...updates } : null,
        })),
      logout: () => {
        localStorage.removeItem('auth_token')
        set({ user: null, token: null })
      },
    }),
    {
      name: 'engvie-user',
      partialize: (state) => ({ token: state.token }),
    }
  )
)
