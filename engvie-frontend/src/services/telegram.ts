declare global {
  interface Window {
    Telegram: {
      WebApp: TelegramWebApp
    }
  }
}

interface TelegramWebApp {
  initData: string
  initDataUnsafe: {
    user?: {
      id: number
      first_name: string
      last_name?: string
      username?: string
      language_code?: string
    }
    auth_date: number
    hash: string
  }
  ready: () => void
  expand: () => void
  close: () => void
  showAlert: (message: string, callback?: () => void) => void
  showConfirm: (message: string, callback: (confirmed: boolean) => void) => void
  HapticFeedback: {
    impactOccurred: (style: 'light' | 'medium' | 'heavy' | 'rigid' | 'soft') => void
    notificationOccurred: (type: 'error' | 'success' | 'warning') => void
    selectionChanged: () => void
  }
  MainButton: {
    text: string
    color: string
    textColor: string
    isVisible: boolean
    isActive: boolean
    show: () => void
    hide: () => void
    enable: () => void
    disable: () => void
    setText: (text: string) => void
    onClick: (callback: () => void) => void
    offClick: (callback: () => void) => void
  }
  BackButton: {
    isVisible: boolean
    show: () => void
    hide: () => void
    onClick: (callback: () => void) => void
    offClick: (callback: () => void) => void
  }
  themeParams: {
    bg_color?: string
    text_color?: string
    hint_color?: string
    link_color?: string
    button_color?: string
    button_text_color?: string
    secondary_bg_color?: string
  }
  colorScheme: 'light' | 'dark'
  viewportHeight: number
  viewportStableHeight: number
  openLink: (url: string) => void
  shareUrl: (url: string, text?: string) => void
}

export const tgApp = window.Telegram?.WebApp

export function initTelegramApp() {
  if (tgApp) {
    tgApp.ready()
    tgApp.expand()

    // Apply theme
    const theme = tgApp.themeParams
    if (theme.bg_color) {
      document.documentElement.style.setProperty('--tg-theme-bg-color', theme.bg_color)
    }
    if (theme.text_color) {
      document.documentElement.style.setProperty('--tg-theme-text-color', theme.text_color)
    }
    if (theme.button_color) {
      document.documentElement.style.setProperty('--tg-theme-button-color', theme.button_color)
    }
    if (theme.secondary_bg_color) {
      document.documentElement.style.setProperty('--tg-theme-secondary-bg-color', theme.secondary_bg_color)
    }
  }
}

export function hapticFeedback(type: 'success' | 'error' | 'warning' | 'light' | 'medium' | 'heavy') {
  if (!tgApp?.HapticFeedback) return
  if (['success', 'error', 'warning'].includes(type)) {
    tgApp.HapticFeedback.notificationOccurred(type as 'success' | 'error' | 'warning')
  } else {
    tgApp.HapticFeedback.impactOccurred(type as 'light' | 'medium' | 'heavy')
  }
}

export function shareGameResult(score: string, rating: number) {
  const text = `🎯 Engvie Battle Result\n${score}\nRating: ${rating} ⭐\nPlay at @EngvieBot`
  if (tgApp?.openLink) {
    tgApp.openLink(`https://t.me/share/url?text=${encodeURIComponent(text)}`)
  }
}

export function getInitData(): string {
  return tgApp?.initData || ''
}

export function getTelegramUser() {
  return tgApp?.initDataUnsafe?.user || null
}
