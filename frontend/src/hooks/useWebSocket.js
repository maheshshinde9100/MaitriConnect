import { useState, useEffect, useCallback } from 'react';
import websocketService from '../services/websocketService';

export const useWebSocket = () => {
  const [messages, setMessages] = useState([]);
  const [connected, setConnected] = useState(false);
  const [error, setError] = useState(null);

  const onMessageReceived = useCallback((message) => {
    setMessages(prev => [...prev, {
      ...message,
      timestamp: message.timestamp || new Date().toISOString(),
      id: message.id || Date.now() + Math.random()
    }]);
  }, []);

  const onConnected = useCallback(() => {
    setConnected(true);
    setError(null);
  }, []);

  const onError = useCallback((error) => {
    setConnected(false);
    setError(error);
  }, []);

  const connect = useCallback(() => {
    websocketService.connect(onMessageReceived, onConnected, onError);
  }, [onMessageReceived, onConnected, onError]);

  const sendMessage = useCallback((content, roomId = 'public') => {
    const username = localStorage.getItem('username');
    const message = {
      senderId: username,
      content: content,
      type: 'CHAT',
      chatRoomId: roomId,
      timestamp: new Date().toISOString()
    };
    websocketService.sendMessage(message);
  }, []);

  const addUser = useCallback(() => {
    const username = localStorage.getItem('username');
    websocketService.addUser(username);
  }, []);

  const disconnect = useCallback(() => {
    websocketService.disconnect();
    setConnected(false);
  }, []);

  const clearMessages = useCallback(() => {
    setMessages([]);
  }, []);

  useEffect(() => {
    return () => {
      disconnect();
    };
  }, [disconnect]);

  return {
    messages,
    connected,
    error,
    connect,
    sendMessage,
    addUser,
    disconnect,
    clearMessages
  };
};