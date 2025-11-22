import { useState } from "react";
import { Send, Paperclip, X, Smile, Image as ImageIcon } from "lucide-react";

export default function MessageInput({ onSend, onFileSelect, selectedFile, onClearFile }) {
  const [message, setMessage] = useState("");

  const handleSend = () => {
    if (message.trim() || selectedFile) {
      onSend(message);
      setMessage("");
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div
      style={{
        padding: 'var(--space-4)',
        borderTop: '1px solid var(--border-primary)',
        background: 'var(--bg-primary)',
      }}
    >
      <div style={{ maxWidth: '900px', margin: '0 auto' }}>
        {/* File Preview */}
        {selectedFile && (
          <div
            className="animate-slide-up"
            style={{
              marginBottom: 'var(--space-3)',
              padding: 'var(--space-3)',
              background: 'var(--bg-secondary)',
              borderRadius: 'var(--radius-md)',
              display: 'flex',
              alignItems: 'center',
              gap: 'var(--space-3)',
            }}
          >
            <ImageIcon size={20} style={{ color: 'var(--primary-500)' }} />
            <div style={{ flex: 1 }}>
              <p className="font-medium" style={{ fontSize: '14px', color: 'var(--text-primary)', marginBottom: '2px' }}>
                {selectedFile.name}
              </p>
              <p style={{ fontSize: '12px', color: 'var(--text-tertiary)' }}>
                {(selectedFile.size / 1024).toFixed(1)} KB
              </p>
            </div>
            <button
              onClick={onClearFile}
              style={{
                padding: 'var(--space-1)',
                background: 'transparent',
                border: 'none',
                cursor: 'pointer',
                borderRadius: 'var(--radius-sm)',
                color: 'var(--text-secondary)',
              }}
            >
              <X size={18} />
            </button>
          </div>
        )}

        {/* Input Area */}
        <div
          style={{
            display: 'flex',
            alignItems: 'flex-end',
            gap: 'var(--space-2)',
          }}
        >
          {/* File Upload Button */}
          <label
            style={{
              padding: 'var(--space-3)',
              background: 'var(--bg-secondary)',
              borderRadius: 'var(--radius-md)',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              transition: 'all 0.2s',
              border: '1px solid var(--border-primary)',
            }}
            onMouseOver={(e) => e.currentTarget.style.background = 'var(--bg-tertiary)'}
            onMouseOut={(e) => e.currentTarget.style.background = 'var(--bg-secondary)'}
          >
            <Paperclip size={20} style={{ color: 'var(--text-secondary)' }} />
            <input
              type="file"
              style={{ display: 'none' }}
              onChange={(e) => onFileSelect(e.target.files[0])}
            />
          </label>

          {/* Message Input */}
          <div style={{ flex: 1, position: 'relative' }}>
            <textarea
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="Type a message..."
              className="input"
              style={{
                minHeight: '48px',
                maxHeight: '120px',
                resize: 'none',
                paddingRight: 'var(--space-10)',
              }}
              rows={1}
            />

            {/* Emoji Button */}
            <button
              style={{
                position: 'absolute',
                right: 'var(--space-3)',
                bottom: 'var(--space-3)',
                background: 'transparent',
                border: 'none',
                cursor: 'pointer',
                padding: 'var(--space-1)',
                borderRadius: 'var(--radius-sm)',
              }}
            >
              <Smile size={20} style={{ color: 'var(--text-muted)' }} />
            </button>
          </div>

          {/* Send Button */}
          <button
            onClick={handleSend}
            disabled={!message.trim() && !selectedFile}
            className="btn-primary"
            style={{
              height: '48px',
              width: '48px',
              padding: 0,
              flexShrink: 0,
              borderRadius: 'var(--radius-md)',
              opacity: (!message.trim() && !selectedFile) ? 0.5 : 1,
              cursor: (!message.trim() && !selectedFile) ? 'not-allowed' : 'pointer',
            }}
          >
            <Send size={20} />
          </button>
        </div>
      </div>
    </div>
  );
}
