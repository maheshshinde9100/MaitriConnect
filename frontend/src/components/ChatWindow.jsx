import { Phone, Search, Video } from 'lucide-react'
import MessageBubble from './MessageBubble'
import MessageInput from './MessageInput'

const ChatWindow = ({ room, messages = [], onSend, isSending, messagesLoading }) => {
  if (!room) {
    return (
      <div className="flex flex-1 flex-col items-center justify-center text-slate-400">
        <p>Select a chat to start messaging</p>
      </div>
    )
  }

  return (
    <div className="flex flex-1 flex-col">
      <div className="flex items-center justify-between border-b border-slate-200 bg-white/80 px-6 py-4">
        <div className="flex items-center gap-3">
          {room.avatar ? (
            <img
              src={room.avatar}
              alt={room.name}
              className="h-12 w-12 rounded-full object-cover"
            />
          ) : (
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-slate-200 text-sm font-semibold text-slate-600">
              {room.name?.slice(0, 2)?.toUpperCase() ?? '?'}
            </div>
          )}
          <div>
            <p className="font-semibold text-slate-900">
              {room.name ?? room.roomName ?? 'Untitled'}
            </p>
            <p className="text-sm text-slate-500">
              {room.typing
                ? 'typing…'
                : room.status ?? `${room.participants?.length ?? 0} members`}
            </p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <button className="rounded-full border border-slate-200 p-2 text-slate-500 hover:bg-slate-50">
            <Search size={18} />
          </button>
          <button className="rounded-full border border-slate-200 p-2 text-slate-500 hover:bg-sky-50 hover:text-sky-600">
            <Phone size={18} />
          </button>
          <button className="rounded-full border border-slate-200 p-2 text-slate-500 hover:bg-sky-50 hover:text-sky-600">
            <Video size={18} />
          </button>
        </div>
      </div>

      <div className="flex-1 space-y-4 overflow-y-auto bg-[url('https://www.transparenttextures.com/patterns/dark-matter.png')] bg-slate-100/80 px-6 py-6 custom-scrollbar">
        {messagesLoading ? (
          <div className="text-center text-sm text-slate-400">Loading messages…</div>
        ) : (
          messages.map((message) => (
            <MessageBubble
              key={message.id}
              message={message}
              isOwn={message.isOwn}
            />
          ))
        )}
      </div>

      <MessageInput onSend={onSend} disabled={isSending} />
    </div>
  )
}

export default ChatWindow
