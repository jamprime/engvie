import { Client, StompSubscription } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

const WS_URL = import.meta.env.VITE_WS_URL || '/ws/game'

class WebSocketService {
  private client: Client | null = null
  private subscriptions: Map<string, StompSubscription> = new Map()
  private subscriptionCallbacks: Map<string, (data: any) => void> = new Map()
  private connected = false
  private connecting = false
  private wasConnected = false
  private connectCallbacks: Array<() => void> = []
  private pendingSubscriptions: Array<{ destination: string; callback: (data: any) => void }> = []
  private pendingMessages: Array<{ destination: string; body: object }> = []
  private reconnectListeners: Set<() => void> = new Set()

  connect(token: string): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.connected) {
        resolve()
        return
      }

      if (this.connecting) {
        this.connectCallbacks.push(resolve)
        return
      }

      this.connecting = true

      this.client = new Client({
        webSocketFactory: () => new SockJS(WS_URL),
        connectHeaders: {
          Authorization: `Bearer ${token}`,
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          const isReconnect = this.wasConnected
          this.connected = true
          this.connecting = false
          this.wasConnected = true

          if (isReconnect) {
            // Re-apply all existing subscriptions after reconnect
            const callbacksCopy = new Map(this.subscriptionCallbacks)
            this.subscriptions.forEach((sub) => {
              try { sub.unsubscribe() } catch {}
            })
            this.subscriptions.clear()
            this.subscriptionCallbacks.clear()
            callbacksCopy.forEach((callback, destination) => {
              this._doSubscribe(destination, callback)
            })
            // Notify reconnect listeners (e.g. Battle.tsx can re-join game)
            this.reconnectListeners.forEach((cb) => cb())
          }

          // Apply pending subscriptions
          this.pendingSubscriptions.forEach(({ destination, callback }) => {
            this._doSubscribe(destination, callback)
          })
          this.pendingSubscriptions = []

          // Send pending messages
          this.pendingMessages.forEach(({ destination, body }) => {
            this._doSend(destination, body)
          })
          this.pendingMessages = []

          resolve()
          this.connectCallbacks.forEach((cb) => cb())
          this.connectCallbacks = []
        },
        onStompError: (error) => {
          this.connecting = false
          reject(new Error(`WebSocket error: ${error.headers?.message}`))
        },
        onDisconnect: () => {
          this.connected = false
        },
      })

      this.client.activate()
    })
  }

  disconnect() {
    this.subscriptions.forEach((sub) => sub.unsubscribe())
    this.subscriptions.clear()
    this.subscriptionCallbacks.clear()
    this.pendingSubscriptions = []
    this.pendingMessages = []
    this.reconnectListeners.clear()
    this.wasConnected = false
    this.client?.deactivate()
    this.connected = false
  }

  subscribe(destination: string, callback: (data: any) => void): void {
    if (!this.client || !this.connected) {
      this.pendingSubscriptions.push({ destination, callback })
      return
    }
    this._doSubscribe(destination, callback)
  }

  private _doSubscribe(destination: string, callback: (data: any) => void): void {
    const sub = this.client!.subscribe(destination, (message) => {
      try {
        const data = JSON.parse(message.body)
        callback(data)
      } catch {
        callback(message.body)
      }
    })
    this.subscriptions.set(destination, sub)
    this.subscriptionCallbacks.set(destination, callback)
  }

  unsubscribe(destination: string) {
    const sub = this.subscriptions.get(destination)
    sub?.unsubscribe()
    this.subscriptions.delete(destination)
    this.subscriptionCallbacks.delete(destination)
  }

  send(destination: string, body: object) {
    if (!this.client || !this.connected) {
      this.pendingMessages.push({ destination, body })
      return
    }
    this._doSend(destination, body)
  }

  private _doSend(destination: string, body: object): void {
    this.client!.publish({
      destination: `/app${destination}`,
      body: JSON.stringify(body),
    })
  }

  /**
   * Register a callback that fires whenever the WebSocket reconnects.
   * Returns an unsubscribe function.
   */
  onReconnect(cb: () => void): () => void {
    this.reconnectListeners.add(cb)
    return () => this.reconnectListeners.delete(cb)
  }

  isConnected() {
    return this.connected
  }
}

export const wsService = new WebSocketService()
