import React, { useEffect, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { ScoreBoard } from '../components/game/ScoreBoard'
import { QuestionCard } from '../components/game/QuestionCard'
import { Timer } from '../components/game/Timer'
import { Loader } from '../components/common/Loader'
import { useUserStore, useGameStore } from '../store'
import { wsService } from '../services/websocket'
import { gamesApi } from '../services/api'
import { hapticFeedback } from '../services/telegram'
import { Game } from '../types/game'

interface OpponentTurn {
  playerId: number
  question: { word: string; direction: string; options: string[] } | null
}

interface OpponentResult {
  isCorrect: boolean
  selectedAnswer: string | null
  correctAnswer: string | null
}

export function Battle() {
  const { gameId } = useParams<{ gameId: string }>()
  const navigate = useNavigate()
  const { user } = useUserStore()
  const {
    activeRound,
    currentScore,
    isMyTurn,
    lastAnswerResult,
    setActiveRound,
    setCurrentScore,
    setGameFinished,
    setIsMyTurn,
    setLastAnswerResult,
    resetGame,
  } = useGameStore()

  const [game, setGame] = useState<Game | null>(null)
  const [loading, setLoading] = useState(true)
  const [selectedAnswer, setSelectedAnswer] = useState<string | null>(null)
  const [opponentTurn, setOpponentTurn] = useState<OpponentTurn | null>(null)
  const [opponentResult, setOpponentResult] = useState<OpponentResult | null>(null)

  // Refs for timing: delay OPPONENT_TURN so player sees their result first
  const answerResultTimeRef = useRef<number | null>(null)
  const opponentTurnTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null)
  const gameFinishedTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null)
  const isBotGameRef = useRef(false)

  useEffect(() => {
    if (!gameId || !user) return
    const id = parseInt(gameId)

    resetGame()
    setOpponentTurn(null)
    setOpponentResult(null)

    gamesApi.getGame(id)
      .then((res) => { setGame(res.data); isBotGameRef.current = res.data.gameType === 'computer'; setLoading(false) })
      .catch(() => navigate('/'))

    const handleGameFinished = (data: any) => {
      const myResult = isBotGameRef.current
        ? data.result  // backend already sets result from P1's perspective
        : (data.winnerId === null
            ? 'tie'
            : (data.winnerId === user?.id ? 'win' : 'loss'))
      const isPlayer1 = user?.id === data.player1Id
      const myRatingChange = isPlayer1 ? data.player1RatingChange : data.player2RatingChange
      const processed = {
        ...data,
        result: myResult,
        ratingChange: myRatingChange,
        coinsEarned: data.coinsEarned ?? 0,
        isBotGame: isBotGameRef.current,
      }
      setGameFinished(processed)
      // Delay so the player sees the final answer result before navigating
      if (gameFinishedTimerRef.current) clearTimeout(gameFinishedTimerRef.current)
      gameFinishedTimerRef.current = setTimeout(() => {
        navigate(`/result/${id}`, { state: processed })
      }, 2000)
    }

    wsService.subscribe(`/topic/game/${id}`, (data) => {
      if (data.type === 'GAME_FINISHED') handleGameFinished(data)
    })

    wsService.subscribe(`/user/queue/game`, (data) => {
      if (data.type === 'ERROR') {
        console.error('Game error:', data.message)
        navigate('/')
        return
      }

      if (data.type === 'ROUND_START') {
        answerResultTimeRef.current = null
        if (opponentTurnTimerRef.current) clearTimeout(opponentTurnTimerRef.current)
        setSelectedAnswer(null)
        setLastAnswerResult(null)
        setOpponentTurn(null)
        setOpponentResult(null)
        setActiveRound({
          roundId: data.roundId,
          question: data.question,
          timeLimit: data.timeLimit || 6000,
          startedAt: Date.now(),
        })
        setIsMyTurn(true)
        return
      }

      if (data.type === 'ANSWER_RESULT') {
        answerResultTimeRef.current = Date.now()
        setLastAnswerResult({ isCorrect: data.isCorrect, correctAnswer: data.correctAnswer })
        setCurrentScore(data.currentScore)
        setIsMyTurn(false)
        hapticFeedback(data.isCorrect ? 'success' : 'error')
        return
      }

      if (data.type === 'OPPONENT_TURN') {
        // Delay showing opponent's view so player has time to see their answer result
        const elapsed = answerResultTimeRef.current !== null ? Date.now() - answerResultTimeRef.current : Infinity
        const delay = Math.max(0, 800 - elapsed)
        if (opponentTurnTimerRef.current) clearTimeout(opponentTurnTimerRef.current)
        const pendingTurn = { playerId: data.playerId, question: data.question || null }
        opponentTurnTimerRef.current = setTimeout(() => {
          setOpponentTurn(pendingTurn)
          setOpponentResult(null)
        }, delay)
        return
      }

      if (data.type === 'OPPONENT_ANSWERED') {
        setOpponentResult({
          isCorrect: data.isCorrect,
          selectedAnswer: data.selectedAnswer || null,
          correctAnswer: data.correctAnswer || null,
        })
        setCurrentScore(data.currentScore)
        hapticFeedback(data.isCorrect ? 'light' : 'warning')
        return
      }

      if (data.type === 'GAME_FINISHED') handleGameFinished(data)
    })

    wsService.send('/game.join', { gameId: id })

    const removeReconnect = wsService.onReconnect(() => {
      wsService.send('/game.join', { gameId: id })
    })

    return () => {
      if (opponentTurnTimerRef.current) clearTimeout(opponentTurnTimerRef.current)
      if (gameFinishedTimerRef.current) clearTimeout(gameFinishedTimerRef.current)
      removeReconnect()
      wsService.unsubscribe(`/topic/game/${id}`)
      wsService.unsubscribe(`/user/queue/game`)
    }
  }, [gameId, user?.id])

  const handleAnswer = (answer: string) => {
    if (!activeRound || !gameId || !user || selectedAnswer) return
    setSelectedAnswer(answer)
    wsService.send('/game.answer', {
      gameId: parseInt(gameId),
      roundId: activeRound.roundId,
      answer,
      answerTimeMs: Date.now() - activeRound.startedAt,
    })
  }

  if (loading) return <Loader fullScreen />
  if (!game || !user) return null

  const opponentName = game.player2 ? game.player2.username : 'Bot'
  const isPlayer1 = user.id === game.player1.id
  const myScore = isPlayer1 ? currentScore.player1 : currentScore.player2
  const currentRound = Math.min(myScore + 1, game.roundsCount)

  return (
    <div className="flex flex-col min-h-screen bg-[var(--tg-theme-bg-color,#fff)]">
      <div className="p-4 bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)]">
        <p className="text-center text-sm font-semibold mb-2">
          🏆 {game.player1.username} vs {game.player2?.username || 'Bot'}
        </p>
        <ScoreBoard
          player1={game.player1}
          player2={game.player2}
          player1Score={currentScore.player1}
          player2Score={currentScore.player2}
          currentRound={currentRound}
          totalRounds={game.roundsCount}
          isMyTurn={isMyTurn}
          myPlayerId={user.id}
        />
      </div>

      <div className="flex-1 p-4">
        <AnimatePresence mode="wait">

          {/* MY TURN — also show after answering until opponent's turn appears */}
          {activeRound && (isMyTurn || (!!selectedAnswer && !opponentTurn)) && (
            <motion.div
              key={`my-turn-${activeRound.roundId}`}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              className="flex flex-col gap-4"
            >
              {/* key forces Timer to remount on each new round */}
              <Timer
                key={activeRound.roundId}
                totalMs={activeRound.timeLimit}
                onExpire={() => { if (!selectedAnswer) handleAnswer('') }}
                paused={!!selectedAnswer}
              />
              <QuestionCard
                word={activeRound.question.word}
                direction={activeRound.question.direction}
                options={activeRound.question.options}
                onAnswer={handleAnswer}
                correctAnswer={lastAnswerResult?.correctAnswer}
                selectedAnswer={selectedAnswer || undefined}
                disabled={!!selectedAnswer}
              />
            </motion.div>
          )}

          {/* OPPONENT'S TURN — show their question */}
          {!isMyTurn && opponentTurn?.question && (
            <motion.div
              key="opponent-turn"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              className="flex flex-col gap-4"
            >
              {/* Header */}
              <div className="flex items-center gap-2 justify-center">
                <motion.span
                  className="text-2xl"
                  animate={opponentResult ? {} : { rotate: [0, 15, -15, 0] }}
                  transition={{ duration: 1.2, repeat: opponentResult ? 0 : Infinity }}
                >
                  {opponentResult
                    ? (opponentResult.isCorrect ? '✅' : '❌')
                    : '🤔'}
                </motion.span>
                <span className="font-semibold text-[var(--tg-theme-hint-color,#757575)]">
                  {opponentResult
                    ? (opponentResult.isCorrect ? `${opponentName} got it right!` : `${opponentName} got it wrong`)
                    : `${opponentName} is thinking...`}
                </span>
              </div>

              {/* Opponent's question (disabled, highlights after answered) */}
              <QuestionCard
                word={opponentTurn.question.word}
                direction={opponentTurn.question.direction as 'en_to_ru' | 'ru_to_en'}
                options={opponentTurn.question.options}
                onAnswer={() => {}}
                correctAnswer={opponentResult?.correctAnswer || undefined}
                selectedAnswer={opponentResult?.selectedAnswer || undefined}
                disabled={true}
              />

              {/* My previous result reminder */}
              {lastAnswerResult && (
                <div
                  className="text-center text-sm px-4 py-2 rounded-xl"
                  style={{
                    backgroundColor: lastAnswerResult.isCorrect ? '#e8f5e9' : '#ffebee',
                    color: lastAnswerResult.isCorrect ? '#2e7d32' : '#c62828',
                  }}
                >
                  Your answer: {lastAnswerResult.isCorrect
                    ? '✓ Correct'
                    : `✗ Wrong — correct: ${lastAnswerResult.correctAnswer}`}
                </div>
              )}
            </motion.div>
          )}

          {/* WAITING — no question yet (initial state or reconnect) */}
          {!isMyTurn && !opponentTurn?.question && !selectedAnswer && (
            <motion.div
              key="waiting"
              className="flex flex-col items-center justify-center gap-4 mt-16"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
            >
              <div className="w-12 h-12 border-4 border-t-blue-500 rounded-full animate-spin" />
              <p className="text-[var(--tg-theme-hint-color,#757575)]">
                Waiting for {opponentName}...
              </p>
            </motion.div>
          )}

        </AnimatePresence>
      </div>
    </div>
  )
}
