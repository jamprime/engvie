import React, { useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Loader } from './components/common/Loader'
import { useAuth } from './hooks/useAuth'
import { useWebSocket } from './hooks/useWebSocket'
import { initTelegramApp } from './services/telegram'

// Pages
import { Home } from './pages/Home'
import { Ranked } from './pages/Ranked'
import { VsComputer } from './pages/VsComputer'
import { Battle } from './pages/Battle'
import { GameResult } from './pages/GameResult'
import { Learning } from './pages/Learning'
import { MyWords } from './pages/Learning/MyWords'
import { Practice } from './pages/Learning/Practice'
import { WordPacks } from './pages/Learning/WordPacks'
import { CategoryWords } from './pages/Learning/CategoryWords'
import { BattleMistakes } from './pages/Learning/BattleMistakes'
import { Leaderboard } from './pages/Leaderboard'
import { DailyTasks } from './pages/DailyTasks'
import { Achievements } from './pages/Achievements'
import { Settings } from './pages/Settings'

function AppInner() {
  const { user, loading } = useAuth()
  useWebSocket()

  if (loading) return <Loader fullScreen text="Loading Engvie..." />
  if (!user) return <Loader fullScreen text="Authenticating..." />

  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/ranked" element={<Ranked />} />
      <Route path="/vs-computer" element={<VsComputer />} />
      <Route path="/battle/:gameId" element={<Battle />} />
      <Route path="/result/:gameId" element={<GameResult />} />
      <Route path="/learning" element={<Learning />} />
      <Route path="/learning/my-words" element={<MyWords />} />
      <Route path="/learning/practice" element={<Practice />} />
      <Route path="/learning/word-packs" element={<WordPacks />} />
      <Route path="/learning/word-packs/:code" element={<CategoryWords />} />
      <Route path="/learning/mistakes" element={<BattleMistakes />} />
      <Route path="/leaderboard" element={<Leaderboard />} />
      <Route path="/daily-tasks" element={<DailyTasks />} />
      <Route path="/achievements" element={<Achievements />} />
      <Route path="/settings" element={<Settings />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

function App() {
  useEffect(() => {
    initTelegramApp()
  }, [])

  return (
    <BrowserRouter>
      <AppInner />
    </BrowserRouter>
  )
}

export default App
