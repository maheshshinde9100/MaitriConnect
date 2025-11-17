import { Pin, VolumeX } from 'lucide-react'

const ChatList = ({ chats = [], selectedChatId, onSelect }) => {
  if (!chats.length) {
    return (
      <div className="rounded-2xl border border-dashed border-slate-200 p-8 text-center text-sm text-slate-500">
        No conversations yet. Start a chat to see it here.
      </div>
    )
  }

  return (
    <div className="space-y-1">
      {chats.map((chat) => {
        const isActive = chat.id === selectedChatId
        const lastMessage =
          typeof chat.lastMessage === 'string'
            ? chat.lastMessage
            : chat.lastMessage?.content
        const lastActive =
          chat.lastActive ?? chat.lastMessage?.timestamp ?? ''
        const unread = chat.unread ?? chat.unreadCount ?? 0

        return (
          <button
            key={chat.id}
            onClick={() => onSelect(chat.id)}
            className={[
              'flex w-full items-start gap-3 rounded-2xl px-3 py-3 text-left transition',
              isActive
                ? 'bg-white shadow-sm shadow-slate-200'
                : 'hover:bg-white/70',
            ].join(' ')}
          >
            <div className="relative">
              {chat.avatar ? (
                <img
                  src={chat.avatar}
                  alt={chat.name}
                  className="h-12 w-12 rounded-full object-cover"
                />
              ) : (
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-gradient-to-br from-slate-200 to-slate-100 text-sm font-semibold text-slate-600">
                  {chat.name?.slice(0, 2)?.toUpperCase() ?? '?'}
                </div>
              )}
              {chat.online && (
                <span className="absolute bottom-0 right-0 h-3 w-3 rounded-full border-2 border-white bg-emerald-400" />
              )}
            </div>

            <div className="flex flex-1 flex-col gap-1">
              <div className="flex items-center gap-2">
                <p className="font-semibold text-slate-900">
                  {chat.name ?? chat.roomName ?? 'Untitled'}
                </p>
                {chat.pinned && (
                  <Pin size={14} className="text-slate-400" strokeWidth={2.5} />
                )}
                {chat.typing && (
                  <span className="text-xs text-sky-500">typing…</span>
                )}
              </div>
              {lastMessage && (
                <p className="text-sm text-slate-500 line-clamp-1">
                  {lastMessage}
                </p>
              )}
            </div>

            <div className="flex flex-col items-end gap-2">
              {lastActive && (
                <p className="text-xs text-slate-400">{lastActive}</p>
              )}
              <div className="flex items-center gap-1">
                {chat.muted && <VolumeX size={14} className="text-slate-400" />}
                {unread > 0 && (
                  <span className="rounded-full bg-sky-500 px-2 py-0.5 text-xs font-semibold text-white">
                    {unread}
                  </span>
                )}
              </div>
            </div>
          </button>
        )
      })}
    </div>
  )
}

export default ChatList

