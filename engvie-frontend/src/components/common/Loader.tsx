import React from 'react'

interface LoaderProps {
  text?: string
  fullScreen?: boolean
}

export function Loader({ text = 'Loading...', fullScreen = false }: LoaderProps) {
  const content = (
    <div className="flex flex-col items-center justify-center gap-3">
      <div className="w-12 h-12 border-4 border-gray-200 border-t-[var(--tg-theme-button-color,#0088cc)] rounded-full animate-spin" />
      {text && <p className="text-sm text-[var(--tg-theme-hint-color,#757575)]">{text}</p>}
    </div>
  )

  if (fullScreen) {
    return (
      <div className="fixed inset-0 flex items-center justify-center bg-[var(--tg-theme-bg-color,#fff)]">
        {content}
      </div>
    )
  }

  return (
    <div className="flex items-center justify-center p-8">
      {content}
    </div>
  )
}
