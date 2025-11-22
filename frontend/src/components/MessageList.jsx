import { useEffect, useRef } from "react";
import { Check, CheckCheck, Smile } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import FilePreview from "./FilePreview";
import MessageReactions from "./MessageReactions";

export default function MessageList({
  messages,
  currentUser,
  typingUser,
  onReact,
  fileAttachments,
}) {
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const formatTime = (timestamp) => {
    if (!timestamp) return "";
    const date = new Date(timestamp);
    return date.toLocaleTimeString("en-US", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: true,
    });
  };

  const groupMessagesByDate = (messages) => {
    const groups = {};
    messages.forEach((msg) => {
      const date = new Date(msg.timestamp || Date.now()).toLocaleDateString();
      if (!groups[date]) groups[date] = [];
      groups[date].push(msg);
    });
    return groups;
  };

  const groupedMessages = groupMessagesByDate(messages);

  return (
    <div
      style={{
        flex: 1,
        overflowY: "auto",
        padding: "var(--space-6)",
        background: "var(--bg-app)",
      }}
    >
      <div style={{ maxWidth: "900px", margin: "0 auto" }}>
        <AnimatePresence>
          {Object.entries(groupedMessages).map(([date, msgs]) => (
            <div key={date}>
              {/* Date Divider */}
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  margin: "var(--space-6) 0",
                }}
              >
                <div style={{ flex: 1, height: "1px", background: "var(--border-secondary)" }}></div>
                <span
                  style={{
                    padding: "0 var(--space-3)",
                    fontSize: "12px",
                    color: "var(--text-muted)",
                    fontWeight: 500,
                  }}
                >
                  {date}
                </span>
                <div style={{ flex: 1, height: "1px", background: "var(--border-secondary)" }}></div>
              </div>

              {/* Messages */}
              {msgs.map((msg, index) => {
                const isSent = msg.senderId === currentUser.userId;
                const showAvatar = !isSent;

                return (
                  <motion.div
                    key={msg.id || index}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="message-bubble"
                    style={{
                      display: "flex",
                      marginBottom: "var(--space-4)",
                      flexDirection: isSent ? "row-reverse" : "row",
                      gap: "var(--space-2)",
                    }}
                  >
                    {/* Avatar for received messages */}
                    {showAvatar && (
                      <div className="avatar avatar-sm" style={{ alignSelf: "flex-end" }}>
                        {msg.senderUsername?.[0]?.toUpperCase() || "U"}
                      </div>
                    )}

                    {/* Message Content */}
                    <div
                      style={{
                        display: "flex",
                        flexDirection: "column",
                        maxWidth: "70%",
                      }}
                    >
                      {/* Message Bubble */}
                      <div
                        className={isSent ? "message-sent" : "message-received"}
                        style={{
                          position: "relative",
                          padding: "var(--space-3) var(--space-4)",
                          borderRadius: "var(--radius-lg)",
                        }}
                      >
                        {/* File Attachment */}
                        {msg.fileAttachments && msg.fileAttachments.length > 0 && (
                          <div style={{ marginBottom: msg.content ? "var(--space-2)" : 0 }}>
                            {msg.fileAttachments.map((fileId) => (
                              <FilePreview
                                key={fileId}
                                fileId={fileId}
                                fileData={fileAttachments[fileId]}
                              />
                            ))}
                          </div>
                        )}

                        {/* Message Text */}
                        {msg.content && (
                          <p style={{ margin: 0, lineHeight: "1.5", fontSize: "14px" }}>
                            {msg.content}
                          </p>
                        )}

                        {/* Message Footer */}
                        <div
                          style={{
                            display: "flex",
                            alignItems: "center",
                            gap: "var(--space-1)",
                            marginTop: "var(--space-1)",
                            justifyContent: "flex-end",
                          }}
                        >
                          <span
                            style={{
                              fontSize: "11px",
                              opacity: 0.7,
                            }}
                          >
                            {formatTime(msg.timestamp)}
                          </span>

                          {/* Read Receipts for sent messages */}
                          {isSent && (
                            <span style={{ display: "flex", alignItems: "center" }}>
                              {msg.readAt ? (
                                <CheckCheck
                                  size={14}
                                  style={{ color: "var(--accent-blue)" }}
                                />
                              ) : msg.deliveredAt ? (
                                <CheckCheck size={14} />
                              ) : (
                                <Check size={14} />
                              )}
                            </span>
                          )}
                        </div>

                        {/* Reactions */}
                        {msg.reactions && Object.keys(msg.reactions).length > 0 && (
                          <MessageReactions
                            reactions={msg.reactions}
                            messageId={msg.id}
                            onReact={onReact}
                            currentUserId={currentUser.userId}
                          />
                        )}
                      </div>
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
            style={{
              display: "flex",
              gap: "var(--space-2)",
              marginBottom: "var(--space-4)",
            }}
          >
            <div className="avatar avatar-sm">
              {typingUser?.[0]?.toUpperCase() || "U"}
            </div>
            <div
              className="message-received"
              style={{
                padding: "var(--space-3)",
                display: "flex",
                gap: "4px",
              }}
            >
              <div className="animate-pulse" style={{ width: "6px", height: "6px", borderRadius: "50%", background: "var(--text-secondary)" }}></div>
              <div className="animate-pulse" style={{ width: "6px", height: "6px", borderRadius: "50%", background: "var(--text-secondary)", animationDelay: "0.2s" }}></div>
              <div className="animate-pulse" style={{ width: "6px", height: "6px", borderRadius: "50%", background: "var(--text-secondary)", animationDelay: "0.4s" }}></div>
            </div>
          </motion.div>
        )}

        <div ref={messagesEndRef} />
      </div>
    </div>
  );
}
