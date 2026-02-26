import React from 'react'
import { classNames } from '../../utils/helpers'

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
  loading?: boolean
  fullWidth?: boolean
}

export function Button({
  children,
  variant = 'primary',
  size = 'md',
  loading = false,
  fullWidth = true,
  className,
  disabled,
  ...props
}: ButtonProps) {
  const base = 'rounded-xl font-medium transition-all duration-200 active:scale-95 flex items-center justify-center gap-2'

  const variants = {
    primary: 'bg-[var(--tg-theme-button-color,#0088cc)] text-[var(--tg-theme-button-text-color,#fff)]',
    secondary: 'border-2 border-[var(--tg-theme-button-color,#0088cc)] text-[var(--tg-theme-button-color,#0088cc)] bg-transparent',
    danger: 'bg-red-500 text-white',
    ghost: 'bg-transparent text-[var(--tg-theme-hint-color,#757575)]',
  }

  const sizes = {
    sm: 'py-2 px-3 text-sm',
    md: 'py-3 px-4 text-base',
    lg: 'py-4 px-6 text-lg',
  }

  return (
    <button
      className={classNames(
        base,
        variants[variant],
        sizes[size],
        fullWidth ? 'w-full' : '',
        (disabled || loading) ? 'opacity-50 cursor-not-allowed' : '',
        className
      )}
      disabled={disabled || loading}
      {...props}
    >
      {loading && (
        <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24" fill="none">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
        </svg>
      )}
      {children}
    </button>
  )
}
