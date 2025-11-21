import { useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Check, CheckCheck } from 'lucide-react';
import MessageReactions from './MessageReactions';
import FilePreview from './FilePreview';

function formatTime(ts) {
  if (!ts) return "";
  const date = new Date(ts);
  return date.toLocaleTimeString("en-US", {
    hour: "2-digit",
    minute: "2-digit",
    hour12: true,
  });
}

function formatDate(ts) {
  if (!ts) return "";
  const date = new Date(ts);
  const today = new Date();
  const yesterday = new Date(today);
  yesterday.setDate(yesterday.getDate() - 1);

  if (date.toDateString() === today.toDateString()) {
    return "Today";
  } else if (date.toDateString() === yesterday.toDateString()) {
    return "Yesterday";
  } else {
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric" });
  }
}

export default function MessageList({ messages, currentUser, typingUser, onReact, fileAttachments = {} }) {
  const messagesEndRef = useRef(null);
  const containerRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const renderReadReceipt = (msg) => {
    if (msg.type !== 'CHAT' && msg.type !== 'FILE') return null;

    const isMine = String(msg.senderId) === String(currentUser?.userId) ||
      msg.senderUsername === currentUser?.username;

    if (!isMine) return null;

    if (msg.status === 'READ' || msg.readAt) {
      return <CheckCheck className="w-3.5 h-3.5 text-blue-400" />;
    } else if (msg.status === 'DELIVERED' || msg.deliveredAt) {
      return <CheckCheck className="w-3.5 h-3.5" style={{ color: 'var(--text-tertiary)' }} />;
    } else {
      return <Check className="w-3.5 h-3.5" style={{ color: 'var(--text-tertiary)' }} />;
    }
  };

  // Group messages by date
  const groupedMessages = messages.reduce((groups, msg) => {
    const date = formatDate(msg.timestamp);
    if (!groups[date]) {
      groups[date] = [];
    }
    groups[date].push(msg);
    return groups;
  }, {});

  return (
    <div ref={containerRef} className="h-full overflow-y-auto px-6 py-4">
      {messages.length === 0 && (
        <div className="flex flex-col items-center justify-center h-full opacity-50">
          <div className="w-20 h-20 rounded-full mb-4 flex items-center justify-center"
            style={{ background: 'var(--bg-tertiary)' }}>
            <motion.div
              animate={{ scale: [1, 1.2, 1] }}
              transition={{ duration: 2, repeat: Infinity }}
              className="text-4xl"
            >
              💬
            </motion.div>
          </div>
          <h3 className="text-xl font-semibold mb-2" style={{ color: 'var(--text-secondary)' }}>
            No messages yet
          </h3>
          <p className="text-sm" style={{ color: 'var(--text-tertiary)' }}>
            Start the conversation with a message!
          </p>
        </div>
      )}

      <AnimatePresence initial={false}>
        {Object.entries(groupedMessages).map(([date, msgs]) => (
          <div key={date}>
            {/* Date Divider */}
            <div className="flex items-center justify-center my-6">
              <div
                className="px-4 py-1.5 rounded-full text-xs font-medium"
                style={{
                  background: 'var(--bg-tertiary)',
                  color: 'var(--text-tertiary)',
                }}
              >
                {date}
              </div>
            </div>

            {/* Messages for this date */}
            {msgs.map((msg, i) => {
              if (msg.type === "JOIN" || msg.type === "LEAVE") {
                return (
                  <motion.div
                    key={i}
                    initial={{ opacity: 0, scale: 0.9 }}
                    animate={{ opacity: 1, scale: 1 }}
                    className="flex justify-center my-3"
                  >
                    <span
                      className="text-xs px-3 py-1.5 rounded-full"
                      style={{
                        background: 'var(--bg-tertiary)',
                        color: 'var(--text-tertiary)',
                      }}
                    >
                      {msg.senderUsername || msg.senderId}{" "}
                      {msg.type === "JOIN" ? "joined" : "left"} the chat
                    </span>
                  </motion.div>
                );
              }

              const isMine =
                String(msg.senderId) === String(currentUser?.userId) ||
                msg.senderUsername === currentUser?.username;

              const prevMsg = msgs[i - 1];
              const showAvatar = !prevMsg || prevMsg.senderId !== msg.senderId;

              return (
                <motion.div
                  key={msg.id || i}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ type: "spring", stiffness: 300, damping: 25 }}
                  className={`flex gap-3 mb-4 ${isMine ? "flex-row-reverse" : "flex-row"}`}
                >
                  {/* Avatar */}
                  <div className="flex-shrink-0">
                    {showAvatar ? (
                      <div
                        className="w-8 h-8 rounded-full flex items-center justify-center text-xs font-semibold text-white"
                        style={{
                          background: isMine
                            ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
                            : 'var(--bg-tertiary)',
                        }}
                      >
                        {(isMine ? currentUser?.username : msg.senderUsername || 'U')[0]?.toUpperCase()}
                      </div>
                    ) : (
                      <div className="w-8"></div>
                    )}
                  </div>

                  {/* Message Content */}
                  <div className={`flex flex-col max-w-[70%] ${isMine ? "items-end" : "items-start"}`}>
                    <div
                      className={`message-bubble px-4 py-2.5 rounded-2xl ${isMine ? "message-bubble-sent" : "message-bubble-received"
                        }`}
                      style={{
                        borderRadius: isMine
                          ? "18px 18px 4px 18px"
                          : "18px 18px 18px 4px",
                      }}
                    >
                      {/* File attachments */}
                      {msg.fileAttachments && msg.fileAttachments.length > 0 && (
                        <div className="mb-2">
                          {msg.fileAttachments.map((fileId) => {
                            const file = fileAttachments[fileId];
                            return file ? (
                              <FilePreview key={fileId} file={file} />
                            ) : null;
                          })}
                        </div>
                      )}

                      {/* Message text */}
                      {msg.content && (
                        <div className="text-sm leading-relaxed whitespace-pre-wrap break-words">
                          {msg.content}
                        </div>
                      )}

                      {/* Timestamp and status */}
                      <div className="flex items-center gap-1.5 mt-1 justify-end">
                        <span
                          className="text-[10px]"
                          style={{
                            color: isMine ? 'rgba(255,255,255,0.7)' : 'var(--text-tertiary)',
                          }}
                        >
                          {formatTime(msg.timestamp)}
                        </span>
                        {renderReadReceipt(msg)}
                      </div>
                    </div>

                    {/* Reactions */}
                    {(msg.reactions && Object.keys(msg.reactions).length > 0) || !isMine ? (
                      <div className="mt-1">
                        <MessageReactions
                          messageId={msg.id}
                          reactions={msg.reactions || {}}
                          onReact={onReact}
                          currentUserId={currentUser?.userId}
                        />
                      </div>
                    ) : null}
                  </div>
                </motion.div>
              );
            })}
          </div>
        ))}
      </AnimatePresence>

      {/* Typing Indicator */}
      {typingUser && (
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: 10 }}
          className="flex gap-3 mb-4"
        >
          <div
            className="w-8 h-8 rounded-full flex items-center justify-center text-xs font-semibold"
            style={{ background: 'var(--bg-tertiary)', color: 'var(--text-secondary)' }}
          >
            {typingUser[0]?.toUpperCase()}
          </div>
          <div
            className="px-4 py-3 rounded-2xl rounded-bl-sm flex items-center gap-1"
            style={{ background: 'var(--bg-tertiary)' }}
          >
            <div className="typing-dot"></div>
            <div className="typing-dot"></div>
            <div className="typing-dot"></div>
          </div>
        </motion.div>
      )}

      <div ref={messagesEndRef} />
    </div>
  );
}
