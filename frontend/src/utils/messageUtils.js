export const formatMessage = (message, currentUsername) => ({
  ...message,
  timestamp: new Date(message.timestamp ?? message.time ?? Date.now()).toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit',
  }),
  isOwn:
    message.senderId === currentUsername ||
    message.sender === 'me' ||
    message.isOwn === true,
})

export const formatMessages = (messages = [], currentUsername) =>
  messages.map((msg) => formatMessage(msg, currentUsername))

