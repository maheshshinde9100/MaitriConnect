import { Phone, Video, MoreVertical, User } from "lucide-react";

export default function ChatHeader({ chatUser }) {
  if (!chatUser) return null;

  return (
    <header
      className="flex items-center justify-between"
      style={{
        padding: 'var(--space-4)',
        borderBottom: '1px solid var(--border-primary)',
        background: 'var(--bg-primary)',
      }}
    >
      {/* User Info */}
      <div className="flex items-center" style={{ gap: 'var(--space-3)' }}>
        <div style={{ position: 'relative' }}>
          <div className="avatar avatar-lg">
            {chatUser.username?.[0]?.toUpperCase() || 'U'}
          </div>
          <div
            className="status status-online"
            style={{
              position: 'absolute',
              bottom: 0,
              right: 0,
              border: `3px solid var(--bg-primary)`,
            }}
          ></div>
        </div>

        <div>
          <h2 className="font-semibold" style={{ fontSize: '16px', color: 'var(--text-primary)', marginBottom: '2px' }}>
            {chatUser.username}
          </h2>
          <p style={{ fontSize: '13px', color: 'var(--text-tertiary)' }}>
            Active now
          </p>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex items-center" style={{ gap: 'var(--space-2)' }}>
        <button className="btn-ghost" style={{ padding: 'var(--space-2)', borderRadius: 'var(--radius-md)' }}>
          <Phone size={18} style={{ color: 'var(--text-secondary)' }} />
        </button>
        <button className="btn-ghost" style={{ padding: 'var(--space-2)', borderRadius: 'var(--radius-md)' }}>
          <Video size={18} style={{ color: 'var(--text-secondary)' }} />
        </button>
        <button className="btn-ghost" style={{ padding: 'var(--space-2)', borderRadius: 'var(--radius-md)' }}>
          <MoreVertical size={18} style={{ color: 'var(--text-secondary)' }} />
        </button>
      </div>
    </header>
  );
}
