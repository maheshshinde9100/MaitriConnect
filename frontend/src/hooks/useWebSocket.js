import { useCallback, useEffect, useRef, useState } from 'react'
import websocketService from '../services/websocketService'

const DEFAULT_DESTINATION = '/app/chat.sendMessage'
const ROOM_TOPIC_PREFIX = '/topic/chatroom/'

export const useWebSocket = (isEnabled = true) => {
  const [connected, setConnected] = useState(false)
  const [error, setError] = useState(null)
  const handlersRef = useRef(new Map())

  const connect = useCallback(() => {
    if (!isEnabled) return

    websocketService.connect(
      () => {
        setConnected(true)
        setError(null)
      },
      (frame) => {
        setConnected(false)
        setError(frame)
      },
    )
  }, [isEnabled])

  const subscribeToRoom = useCallback((roomId, handler) => {
    if (!roomId || !connected) return
    const topic = `${ROOM_TOPIC_PREFIX}${roomId}`
    handlersRef.current.set(topic, handler)
    websocketService.subscribe(topic, handler)
  }, [connected])

  const unsubscribeFromRoom = useCallback((roomId) => {
    if (!roomId) return
    const topic = `${ROOM_TOPIC_PREFIX}${roomId}`
    handlersRef.current.delete(topic)
    websocketService.unsubscribe(topic)
  }, [])

  const sendRoomMessage = useCallback((payload) => {
    websocketService.sendMessage(DEFAULT_DESTINATION, payload)
  }, [])

  const disconnect = useCallback(() => {
    websocketService.disconnect()
    setConnected(false)
  }, [])

  useEffect(() => {
    if (!isEnabled) return
    connect()
    return () => {
      disconnect()
    }
  }, [connect, disconnect, isEnabled])

  useEffect(() => {
    if (!connected) return
    handlersRef.current.forEach((handler, topic) => {
      websocketService.subscribe(topic, handler)
    })
  }, [connected])

  return {
    connected,
    error,
    subscribeToRoom,
    unsubscribeFromRoom,
    sendRoomMessage,
  }
}

