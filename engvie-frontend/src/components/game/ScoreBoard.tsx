import React from 'react'
import { PlayerInfo } from '../../types/game'

interface ScoreBoardProps {
  player1: PlayerInfo
  player2?: PlayerInfo
  player1Score: number
  player2Score: number
  currentRound: number
  totalRounds: number
  isMyTurn: boolean
  myPlayerId: number
}

export function ScoreBoard({
  player1,
  player2,
  player1Score,
  player2Score,
  currentRound,
  totalRounds,
  isMyTurn,
  myPlayerId,
}: ScoreBoardProps) {
  const isPlayer1 = myPlayerId === player1.id

  return (
    <div className="bg-[var(--tg-theme-secondary-bg-color,#f5f5f5)] rounded-2xl p-4">
      <div className="text-center text-sm text-[var(--tg-theme-hint-color,#757575)] mb-3">
        Round {currentRound}/{totalRounds}
      </div>
      <div className="flex items-center justify-between">
        <div className={`flex flex-col items-center ${isPlayer1 ? 'text-[var(--tg-theme-button-color,#0088cc)]' : ''}`}>
          <div className="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center text-lg font-bold mb-1">
            {player1.username[0].toUpperCase()}
          </div>
          <span className="text-xs truncate max-w-[80px]">{isPlayer1 ? 'You' : player1.username}</span>
          <span className="text-2xl font-bold">{player1Score}</span>
        </div>

        <div className="flex flex-col items-center">
          <span className="text-3xl font-bold text-[var(--tg-theme-hint-color,#757575)]">VS</span>
          <span className="text-xs mt-1" style={{ color: isMyTurn ? '#4caf50' : '#ff9800' }}>
            {isMyTurn ? 'Your turn' : 'Opponent'}
          </span>
        </div>

        <div className={`flex flex-col items-center ${!isPlayer1 ? 'text-[var(--tg-theme-button-color,#0088cc)]' : ''}`}>
          <div className="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center text-lg font-bold mb-1">
            {player2 ? player2.username[0].toUpperCase() : '🤖'}
          </div>
          <span className="text-xs truncate max-w-[80px]">
            {player2 ? (!isPlayer1 ? 'You' : player2.username) : 'Bot'}
          </span>
          <span className="text-2xl font-bold">{player2Score}</span>
        </div>
      </div>
    </div>
  )
}
