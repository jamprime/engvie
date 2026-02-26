import { useEffect } from 'react'
import { tgApp, hapticFeedback } from '../services/telegram'

export function useTelegramBackButton(callback: () => void, enabled = true) {
  useEffect(() => {
    if (!tgApp?.BackButton || !enabled) return

    tgApp.BackButton.show()
    tgApp.BackButton.onClick(callback)

    return () => {
      tgApp.BackButton.offClick(callback)
      tgApp.BackButton.hide()
    }
  }, [callback, enabled])
}

export function useTelegram() {
  return {
    app: tgApp,
    haptic: hapticFeedback,
    isDark: tgApp?.colorScheme === 'dark',
    user: tgApp?.initDataUnsafe?.user,
  }
}
