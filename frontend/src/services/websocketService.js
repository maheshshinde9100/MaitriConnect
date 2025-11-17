import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

const WS_URL = import.meta.env.VITE_WS_URL ?? 'http://localhost:8082/ws'

class WebSocketService {
  constructor() {
    this.client = null
    this.connected = false
    this.roomSubscriptions = new Map()
    this.publicHandlers = new Set()
  }

  activate() {
    if (this.client) return

    this.client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    })
  }

  connect(onConnected, onError) {
    if (this.connected) {
      onConnected?.()
      return
    }

    this.activate()

    this.client.onConnect = (frame) => {
      console.info('[WebSocket] Connected', frame)
      this.connected = true
      onConnected?.()
    }

    this.client.onStompError = (frame) => {
      console.error('[WebSocket] Broker error', frame.body)
      this.connected = false
      onError?.(frame)
    }

    this.client.onWebSocketClose = () => {
      console.warn('[WebSocket] Connection closed')
      this.connected = false
    }

    this.client.activate()
  }

  subscribe(topic, handler) {
    if (!this.connected || this.roomSubscriptions.has(topic)) {
      return
    }

    const subscription = this.client.subscribe(topic, (message) => {
      handler(JSON.parse(message.body))
    })

    this.roomSubscriptions.set(topic, subscription)
  }

  unsubscribe(topic) {
    const subscription = this.roomSubscriptions.get(topic)
    if (subscription) {
      subscription.unsubscribe()
      this.roomSubscriptions.delete(topic)
    }
  }

  subscribePublic(handler) {
    if (!this.connected) return
    if (this.publicSubscription) return

    this.publicHandlers.add(handler)
    this.publicSubscription = this.client.subscribe('/topic/public', (message) => {
      const payload = JSON.parse(message.body)
      this.publicHandlers.forEach((cb) => cb(payload))
    })
  }

  sendMessage(destination, payload) {
    if (!this.connected) {
      console.warn('[WebSocket] Not connected, dropping message')
      return
    }

    this.client.publish({
      destination,
      body: JSON.stringify(payload),
    })
  }

  disconnect() {
    this.roomSubscriptions.forEach((subscription) => subscription.unsubscribe())
    this.roomSubscriptions.clear()
    if (this.publicSubscription) {
      this.publicSubscription.unsubscribe()
      this.publicSubscription = null
    }

    if (this.client) {
      this.client.deactivate()
      this.client = null
    }
    this.connected = false
  }
}

const websocketService = new WebSocketService()

export default websocketService

