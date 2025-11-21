import { Phone, Video, MoreVertical, Search } from "lucide-react";

export default function ChatHeader({ chatUser }) {
  if (!chatUser) return null;

  return (
    <header
      className="h-16 px-6 flex items-center justify-between border-b"
      style={{
        background: 'var(--bg-secondary)',
        borderColor: 'var(--border-color)',
      }}
    >
      {/* User Info */}
      <div className="flex items-center gap-3">
        <div className="relative">
          <div
            className="w-10 h-10 rounded-full flex items-center justify-center font-semibold text-white gradient-primary"
          >
            {chatUser.username?.[0]?.toUpperCase() || 'U'}
          </div>
          <span
            className="absolute bottom-0 right-0 w-3 h-3 rounded-full border-2 status-online"
            style={{ borderColor: 'var(--bg-secondary)' }}
          ></span>
        </div>
        <div>
          <h2 className="font-semibold text-base" style={{ color: 'var(--text-primary)' }}>
            {chatUser.username}
          </h2>
          <p className="text-xs" style={{ color: 'var(--text-tertiary)' }}>
            Active now
          </p>
        </div>
      </div>

      {/* Actions */}
      <div className="flex items-center gap-2">
        <button
          className="p-2.5 rounded-lg hover-lift"
          style={{ background: 'var(--bg-tertiary)' }}
          title="Search in conversation"
        >
          <Search className="w-4 h-4" style={{ color: 'var(--text-secondary)' }} />
        </button>
        <button
          className="p-2.5 rounded-lg hover-lift"
          style={{ background: 'var(--bg-tertiary)' }}
          title="Voice call"
        >
          <Phone className="w-4 h-4" style={{ color: 'var(--text-secondary)' }} />
        </button>
        <button
          className="p-2.5 rounded-lg hover-lift"
          style={{ background: 'var(--bg-tertiary)' }}
          title="Video call"
        >
          <Video className="w-4 h-4" style={{ color: 'var(--text-secondary)' }} />
        </button>
        <button
          className="p-2.5 rounded-lg hover-lift"
          style={{ background: 'var(--bg-tertiary)' }}
          title="More options"
        >
          <MoreVertical className="w-4 h-4" style={{ color: 'var(--text-secondary)' }} />
        </button>
      </div>
    </header>
  );
}
