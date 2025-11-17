import { Bell, Image, Info, Link2, Pin, Star } from 'lucide-react'

const ProfilePanel = ({ chat }) => {
  if (!chat) return null

  return (
    <aside className="hidden w-[300px] flex-col border-l border-slate-200 bg-white/70 p-6 lg:flex">
      <div className="flex flex-col items-center text-center">
        <div className="relative">
          {chat.avatar ? (
            <img
              src={chat.avatar}
              alt={chat.name}
              className="h-20 w-20 rounded-full object-cover"
            />
          ) : (
            <div className="flex h-20 w-20 items-center justify-center rounded-full bg-slate-200 text-lg font-semibold text-slate-600">
              {chat.name?.slice(0, 2)?.toUpperCase() ?? '?'}
            </div>
          )}
          {chat.online && (
            <span className="absolute bottom-1 right-1 h-3 w-3 rounded-full border-2 border-white bg-emerald-400" />
          )}
        </div>
        <p className="mt-4 text-lg font-semibold text-slate-900">
          {chat.name ?? chat.roomName}
        </p>
        <p className="text-sm text-slate-500">
          {chat.status ??
            (chat.participants?.length
              ? `${chat.participants.length} members`
              : 'Conversation')}
        </p>
      </div>

      <div className="mt-6 space-y-3">
        <button className="flex w-full items-center justify-between rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-600 hover:bg-slate-50">
          <span className="flex items-center gap-3">
            <Bell size={16} />
            Notifications
          </span>
          <span className="text-xs text-slate-400">Enabled</span>
        </button>
        <button className="flex w-full items-center justify-between rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-600 hover:bg-slate-50">
          <span className="flex items-center gap-3">
            <Pin size={16} />
            Pin conversation
          </span>
        </button>
        <button className="flex w-full items-center justify-between rounded-2xl border border-slate-200 px-4 py-3 text-sm text-slate-600 hover:bg-slate-50">
          <span className="flex items-center gap-3">
            <Star size={16} />
            Star messages
          </span>
        </button>
      </div>

      <div className="mt-8 space-y-4">
        <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">
          Shared content
        </p>
        <div className="space-y-3 text-sm text-slate-600">
          <div className="flex items-center gap-3 rounded-2xl border border-slate-200 px-4 py-3">
            <Image size={16} />
            Media · coming soon
          </div>
          <div className="flex items-center gap-3 rounded-2xl border border-slate-200 px-4 py-3">
            <Link2 size={16} />
            Links · syncing
          </div>
          <div className="flex items-center gap-3 rounded-2xl border border-slate-200 px-4 py-3">
            <Info size={16} />
            Group info & permissions
          </div>
        </div>
      </div>
    </aside>
  )
}

export default ProfilePanel

