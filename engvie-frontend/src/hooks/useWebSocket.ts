import { useCallback, useEffect, useRef } from 'react'
import { wsService } from '../services/websocket'
import { useUserStore } from '../store'

export function useWebSocket() {
  const { token } = useUserStore()
  const connectedRef = useRef(false)

  const connect = useCallback(async () => {
    if (!token || connectedRef.current) return
    try {
      await wsService.connect(token)
      connectedRef.current = true
    } catch (err) {
      console.error('WebSocket connection failed:', err)
    }
  }, [token])

  const disconnect = useCallback(() => {
    wsService.disconnect()
    connectedRef.current = false
  }, [])

  useEffect(() => {
    if (token) connect()
    return () => {
      // Don't disconnect on unmount - keep persistent connection
    }
  }, [token, connect])

  return { connect, disconnect, wsService }
}
