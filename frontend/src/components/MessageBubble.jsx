import { Check, CheckCheck } from 'lucide-react'

const MessageBubble = ({ message, isOwn }) => {
  const timestamp = message.timestamp ?? message.time
  return (
    <div className={`flex ${isOwn ? 'justify-end' : 'justify-start'}`}>
      <div
        className={[
          'max-w-xl rounded-3xl px-4 py-2.5 text-sm shadow-sm',
          isOwn
            ? 'rounded-br-md bg-sky-500 text-white'
            : 'rounded-bl-md bg-white text-slate-800',
        ].join(' ')}
      >
        <p>{message.content}</p>
        <div
          className={[
            'mt-1 flex items-center gap-1 text-[11px]',
            isOwn ? 'text-white/70' : 'text-slate-400',
          ].join(' ')}
        >
          {timestamp && <span>{timestamp}</span>}
          {message.status && isOwn && (
            message.status === 'read' ? (
              <CheckCheck size={14} />
            ) : (
              <Check size={14} />
            )
          )}
        </div>
      </div>
    </div>
  )
}

export default MessageBubble

