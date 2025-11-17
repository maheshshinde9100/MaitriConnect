import { useState } from 'react'
import { Laugh, Mic, Paperclip, Send, Smile } from 'lucide-react'

const MessageInput = ({ onSend, disabled }) => {
  const [value, setValue] = useState('')

  const handleSend = () => {
    if (!value.trim() || disabled) return
    onSend?.(value.trim())
    setValue('')
  }

  const handleKeyDown = (event) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault()
      handleSend()
    }
  }

  return (
    <div className="border-t border-slate-200 bg-white/90 px-6 py-4">
      <div className="flex items-center gap-3 rounded-full border border-slate-200 bg-white px-4 py-2">
        <button className="text-slate-400 hover:text-slate-900">
          <Smile size={20} />
        </button>
        <button className="text-slate-400 hover:text-slate-900">
          <Paperclip size={20} />
        </button>
        <textarea
          rows={1}
          value={value}
          onChange={(event) => setValue(event.target.value)}
          onKeyDown={handleKeyDown}
          disabled={disabled}
          className="flex-1 resize-none border-none bg-transparent text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none disabled:opacity-60"
          placeholder="Message"
        />
        <div className="flex items-center gap-2 text-slate-400">
          <button className="hover:text-slate-900">
            <Laugh size={20} />
          </button>
          <button className="hover:text-slate-900">
            <Mic size={20} />
          </button>
        </div>
        <button
          onClick={handleSend}
          disabled={disabled || !value.trim()}
          className="flex h-9 w-9 items-center justify-center rounded-full bg-sky-500 text-white shadow-lg shadow-sky-500/30 transition hover:bg-sky-600 disabled:cursor-not-allowed disabled:opacity-60"
        >
          <Send size={18} />
        </button>
      </div>
    </div>
  )
}

export default MessageInput
