import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Send, Users, Wifi, WifiOff, Hash } from 'lucide-react';
import { useWebSocket } from '../hooks/useWebSocket';
import { useAuth } from '../contexts/AuthContext';
import { getRoomMessages } from '../services/chatService';
import toast from 'react-hot-toast';

const Chat = ({ roomId = 'public' }) => {
  const [messageInput, setMessageInput] = useState('');
  const [roomMessages, setRoomMessages] = useState([]);
  const messagesEndRef = useRef(null);
  const { user } = useAuth();
  
  const {
    messages,
    connected,
    error,
    connect,
    sendMessage,
    addUser,
    disconnect,
    clearMessages
  } = useWebSocket();

  // Load room messages when room changes
  useEffect(() => {
    const loadRoomMessages = async () => {
      if (roomId !== 'public') {
        try {
          const messages = await getRoomMessages(roomId);
          setRoomMessages(messages);
        } catch (error) {
          console.error('Failed to load room messages:', error);
        }
      } else {
        setRoomMessages([]);
      }
    };

    loadRoomMessages();
    clearMessages(); // Clear WebSocket messages when switching rooms
  }, [roomId, clearMessages]);

  useEffect(() => {
    connect();
    return () => disconnect();
  }, [connect, disconnect]);

  useEffect(() => {
    if (connected && user) {
      addUser();
      toast.success('Connected to chat!');
    }
  }, [connected, addUser, user]);

  useEffect(() => {
    if (error) {
      toast.error('Connection error occurred');
    }
  }, [error]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSendMessage = (e) => {
    e.preventDefault();
    if (messageInput.trim() && connected) {
      sendMessage(messageInput, roomId);
      setMessageInput('');
    }
  };

  // Combine room messages and live WebSocket messages
  const allMessages = [...roomMessages, ...messages];

  const formatTime = (timestamp) => {
    return new Date(timestamp).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getMessageVariant = (message) => {
    if (message.type === 'JOIN') return 'join';
    if (message.type === 'LEAVE') return 'leave';
    if (message.senderId === user?.username) return 'own';
    return 'other';
  };

  return (
    <div className="flex flex-col h-screen">
      {/* Header */}
      <div className="bg-secondary border-b border-border-color p-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-2">
              {roomId === 'public' ? (
                <Hash size={24} className="text-accent" />
              ) : (
                <Users size={24} className="text-accent" />
              )}
              <h2 className="text-xl font-semibold">
                {roomId === 'public' ? 'Public Chat' : `Room ${roomId}`}
              </h2>
            </div>
          </div>
          
          <div className="flex items-center gap-2">
            {connected ? (
              <div className="flex items-center gap-2 text-success">
                <Wifi size={20} />
                <span className="text-sm font-medium">Connected</span>
              </div>
            ) : (
              <div className="flex items-center gap-2 text-error">
                <WifiOff size={20} />
                <span className="text-sm font-medium">Disconnected</span>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Messages Container */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        <AnimatePresence>
          {allMessages.map((message, index) => {
            const variant = getMessageVariant(message);
            
            return (
              <motion.div
                key={message.id || index}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
                transition={{ duration: 0.3 }}
                className={`flex ${variant === 'own' ? 'justify-end' : 'justify-start'}`}
              >
                {variant === 'join' || variant === 'leave' ? (
                  <div className="text-center w-full">
                    <div className="inline-block px-3 py-1 bg-secondary rounded-full text-sm text-muted">
                      <span className="font-medium">{message.senderId}</span>
                      {variant === 'join' ? ' joined the chat' : ' left the chat'}
                      <span className="ml-2 text-xs">
                        {formatTime(message.timestamp)}
                      </span>
                    </div>
                  </div>
                ) : (
                  <div className={`max-w-xs lg:max-w-md ${variant === 'own' ? 'ml-auto' : 'mr-auto'}`}>
                    <div
                      className={`rounded-lg p-3 ${
                        variant === 'own'
                          ? 'bg-accent text-white'
                          : 'bg-secondary text-primary'
                      }`}
                    >
                      {variant === 'other' && (
                        <div className="text-xs font-medium mb-1 opacity-75">
                          {message.senderId}
                        </div>
                      )}
                      <div className="break-words">{message.content}</div>
                      <div className={`text-xs mt-1 ${variant === 'own' ? 'text-white/70' : 'text-muted'}`}>
                        {formatTime(message.timestamp)}
                      </div>
                    </div>
                  </div>
                )}
              </motion.div>
            );
          })}
        </AnimatePresence>
        <div ref={messagesEndRef} />
        
        {allMessages.length === 0 && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="text-center py-8"
          >
            <div className="text-muted">
              {roomId === 'public' ? (
                <>
                  <Hash size={48} className="mx-auto mb-4 opacity-50" />
                  <p>Welcome to the public chat!</p>
                  <p className="text-sm mt-2">Start a conversation by sending a message below.</p>
                </>
              ) : (
                <>
                  <Users size={48} className="mx-auto mb-4 opacity-50" />
                  <p>No messages in this room yet.</p>
                  <p className="text-sm mt-2">Be the first to start the conversation!</p>
                </>
              )}
            </div>
          </motion.div>
        )}
      </div>

      {/* Message Input */}
      <div className="border-t border-border-color p-4">
        <form onSubmit={handleSendMessage} className="flex gap-3">
          <div className="flex-1">
            <input
              type="text"
              value={messageInput}
              onChange={(e) => setMessageInput(e.target.value)}
              placeholder={connected ? "Type your message..." : "Connecting..."}
              disabled={!connected}
              className="form-input"
            />
          </div>
          <button
            type="submit"
            disabled={!connected || !messageInput.trim()}
            className="btn btn-primary"
          >
            <Send size={20} />
          </button>
        </form>
      </div>
    </div>
  );
};

export default Chat;