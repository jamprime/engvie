/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#0088cc',
        success: '#4caf50',
        error: '#f44336',
        warning: '#ff9800',
        surface: '#f5f5f5',
        'text-primary': '#212121',
        'text-secondary': '#757575',
      },
      fontFamily: {
        sans: ['Roboto', 'system-ui', 'sans-serif'],
      },
      animation: {
        'pulse-warning': 'pulse 0.5s ease-in-out infinite',
        'shake': 'shake 0.5s ease-in-out',
        'flash-success': 'flashSuccess 0.5s ease-in-out',
        'flash-error': 'flashError 0.5s ease-in-out',
      },
      keyframes: {
        shake: {
          '0%, 100%': { transform: 'translateX(0)' },
          '10%, 30%, 50%, 70%, 90%': { transform: 'translateX(-4px)' },
          '20%, 40%, 60%, 80%': { transform: 'translateX(4px)' },
        },
        flashSuccess: {
          '0%': { backgroundColor: 'transparent' },
          '50%': { backgroundColor: '#4caf5040' },
          '100%': { backgroundColor: 'transparent' },
        },
        flashError: {
          '0%': { backgroundColor: 'transparent' },
          '50%': { backgroundColor: '#f4433640' },
          '100%': { backgroundColor: 'transparent' },
        },
      },
    },
  },
  plugins: [],
}
