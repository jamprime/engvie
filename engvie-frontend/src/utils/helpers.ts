export function formatTime(seconds: number): string {
  if (seconds < 60) return `${seconds}s`
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}m ${s}s`
}

export function formatTimeUntil(isoDate: string): string {
  const target = new Date(isoDate).getTime()
  const now = Date.now()
  const diff = Math.max(0, target - now)
  const hours = Math.floor(diff / 3600000)
  const minutes = Math.floor((diff % 3600000) / 60000)
  if (hours > 0) return `${hours}h ${minutes}m`
  return `${minutes}m`
}

export function formatTimerColor(timeLeft: number, total: number): string {
  const percent = timeLeft / total
  if (percent > 0.5) return '#4caf50'
  if (percent > 0.25) return '#ff9800'
  return '#f44336'
}

export function getRankColor(rank: string): string {
  const colors: Record<string, string> = {
    BRONZE: '#cd7f32',
    SILVER: '#9e9e9e',
    GOLD: '#ffc107',
    PLATINUM: '#78909c',
    DIAMOND: '#26c6da',
    MASTER: '#9c27b0',
    GRANDMASTER: '#f44336',
  }
  return colors[rank] || '#9e9e9e'
}

export function getEnergyEmoji(energy: number, max: number): string {
  return '⚡'.repeat(energy) + '○'.repeat(max - energy)
}

export function calculateWinRate(wins: number, total: number): string {
  if (total === 0) return '0%'
  return `${Math.round((wins / total) * 100)}%`
}

export function classNames(...classes: (string | undefined | null | false)[]): string {
  return classes.filter(Boolean).join(' ')
}
