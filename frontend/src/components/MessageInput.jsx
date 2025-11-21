import { useState, useRef } from "react";
import { Send, Paperclip, X, Smile, Image as ImageIcon } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

export default function MessageInput({ onSend, onFileSelect, selectedFile, onClearFile }) {
  const [input, setInput] = useState("");
  const textareaRef = useRef();
  const fileInputRef = useRef();

  const handleInput = (e) => {
    setInput(e.target.value);
    textareaRef.current.style.height = "auto";
    textareaRef.current.style.height = Math.min(textareaRef.current.scrollHeight, 120) + "px";
  };

  const handleSend = (e) => {
    e.preventDefault();
    if (input.trim() === "" && !selectedFile) return;
    onSend(input);
    setInput("");
    textareaRef.current.style.height = "auto";
  };

  const handleFileClick = () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (file && onFileSelect) {
      onFileSelect(file);
    }
  };

  return (
    <div className="p-4">
      <form onSubmit={handleSend} className="space-y-3">
        {/* File Preview */}
        <AnimatePresence>
          {selectedFile && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: "auto" }}
              exit={{ opacity: 0, height: 0 }}
              className="flex items-center gap-3 p-3 rounded-xl"
              style={{ background: 'var(--bg-tertiary)', border: '1px solid var(--border-color)' }}
            >
              <div
                className="w-10 h-10 rounded-lg flex items-center justify-center"
                style={{ background: 'var(--bg-hover)' }}
              >
                <ImageIcon className="w-5 h-5" style={{ color: 'var(--text-secondary)' }} />
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium truncate" style={{ color: 'var(--text-primary)' }}>
                  {selectedFile.name}
                </p>
                <p className="text-xs" style={{ color: 'var(--text-tertiary)' }}>
                  {(selectedFile.size / 1024).toFixed(2)} KB
                </p>
              </div>
              <button
                type="button"
                onClick={onClearFile}
                className="p-2 rounded-lg hover-lift"
                style={{ background: 'var(--bg-hover)' }}
              >
                <X className="w-4 h-4" style={{ color: 'var(--text-secondary)' }} />
              </button>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Input Container */}
        <div
          className="flex items-end gap-2 p-2 rounded-2xl"
          style={{ background: 'var(--bg-tertiary)', border: '1px solid var(--border-color)' }}
        >
          {/* Action Buttons */}
          <div className="flex items-center gap-1 pb-2">
            <button
              type="button"
              onClick={handleFileClick}
              className="p-2 rounded-lg hover-lift"
              style={{ background: 'var(--bg-hover)' }}
              title="Attach file"
            >
              <Paperclip className="w-4 h-4" style={{ color: 'var(--text-secondary)' }} />
            </button>
            <input
              ref={fileInputRef}
              type="file"
              className="hidden"
              onChange={handleFileChange}
              accept="image/*,.pdf,.doc,.docx,.xls,.xlsx,.txt"
            />
          </div>

          {/* Text Input */}
          <textarea
            ref={textareaRef}
            className="flex-1 resize-none bg-transparent px-2 py-2 text-sm outline-none"
            style={{ color: 'var(--text-primary)', maxHeight: '120px' }}
            placeholder="Type a message..."
            value={input}
            onChange={handleInput}
            rows={1}
            onKeyDown={(e) => {
              if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                handleSend(e);
              }
            }}
          />

          {/* Send Button */}
          <motion.button
            type="submit"
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="p-2.5 rounded-xl btn-primary flex-shrink-0"
            disabled={!input.trim() && !selectedFile}
            style={{
              opacity: !input.trim() && !selectedFile ? 0.5 : 1,
              cursor: !input.trim() && !selectedFile ? 'not-allowed' : 'pointer',
            }}
          >
            <Send className="w-4 h-4" />
          </motion.button>
        </div>
      </form>
    </div>
  );
}
