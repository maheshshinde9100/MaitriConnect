import { Search, LogOut, MessageSquare, User, X } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

export default function Sidebar({
  user,
  users,
  loading,
  search,
  setSearch,
  onSelectUser,
  logout,
  currentChatUser,
  isMobile,
  isOpen,
  onClose
}) {
  // If mobile and not open, don't render anything (or render hidden)
  // But for animation we might want to keep it mounted. 
  // Let's use simple conditional rendering for now or CSS transform.

  const sidebarStyle = isMobile ? {
    position: 'absolute',
    top: 0,
    left: 0,
    bottom: 0,
    width: '85%',
    maxWidth: '360px',
    zIndex: 50,
    background: 'var(--bg-primary)',
    borderRight: '1px solid var(--border-primary)',
    transform: isOpen ? 'translateX(0)' : 'translateX(-100%)',
    transition: 'transform 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
    boxShadow: isOpen ? 'var(--shadow-xl)' : 'none',
  } : {
    width: '360px',
    background: 'var(--bg-primary)',
    borderRight: '1px solid var(--border-primary)',
    display: 'flex',
    flexDirection: 'column',
  };

  return (
    <>
      {/* Mobile Backdrop */}
      {isMobile && isOpen && (
        <div
          onClick={onClose}
          style={{
            position: 'absolute',
            inset: 0,
            background: 'rgba(0,0,0,0.5)',
            backdropFilter: 'blur(4px)',
            zIndex: 40,
          }}
          className="animate-fade-in"
        />
      )}

      <aside
        className="flex flex-col"
        style={sidebarStyle}
      >
        {/* User Profile Header */}
        <div style={{ padding: 'var(--space-4)', borderBottom: '1px solid var(--border-secondary)' }}>
          <div className="flex items-center justify-between" style={{ marginBottom: 'var(--space-4)' }}>
            <div className="flex items-center" style={{ gap: 'var(--space-3)' }}>
              <div className="avatar avatar-md">
                {user?.username?.[0]?.toUpperCase() || 'U'}
              </div>
              <div>
                <h2 className="font-semibold" style={{ fontSize: '16px', color: 'var(--text-primary)', marginBottom: '2px' }}>
                  {user?.username || 'User'}
                </h2>
                <div className="flex items-center" style={{ gap: 'var(--space-1)' }}>
                  <div className="status status-online"></div>
                  <span style={{ fontSize: '12px', color: 'var(--text-tertiary)' }}>Active now</span>
                </div>
              </div>
            </div>

            <div className="flex items-center" style={{ gap: 'var(--space-2)' }}>
              {isMobile && (
                <button onClick={onClose} className="btn-ghost" style={{ padding: 'var(--space-2)', borderRadius: 'var(--radius-md)' }}>
                  <X size={20} style={{ color: 'var(--text-secondary)' }} />
                </button>
              )}
              <button onClick={logout} className="btn-ghost" style={{ padding: 'var(--space-2)', borderRadius: 'var(--radius-md)' }} title="Logout">
                <LogOut size={18} style={{ color: 'var(--text-secondary)' }} />
              </button>
            </div>
          </div>

          {/* Search Bar */}
          <div style={{ position: 'relative' }}>
            <Search
              size={18}
              style={{
                position: 'absolute',
                left: 'var(--space-3)',
                top: '50%',
                transform: 'translateY(-50%)',
                color: 'var(--text-muted)',
              }}
            />
            <input
              type="text"
              placeholder="Search conversations..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="input"
              style={{
                paddingLeft: 'var(--space-10)',
                fontSize: '14px',
              }}
            />
          </div>
        </div>

        {/* Conversations Header */}
        <div
          className="flex items-center justify-between"
          style={{
            padding: '14px var(--space-4)',
            borderBottom: '1px solid var(--border-secondary)',
          }}
        >
          <div className="flex items-center" style={{ gap: 'var(--space-2)' }}>
            <MessageSquare size={16} style={{ color: 'var(--text-secondary)' }} />
            <span className="font-semibold text-sm" style={{ color: 'var(--text-secondary)' }}>
              All Users
            </span>
          </div>
          <div className="flex items-center" style={{ gap: 'var(--space-2)' }}>
            <button
              onClick={() => window.location.reload()}
              className="btn-ghost"
              style={{ padding: '4px', borderRadius: 'var(--radius-sm)' }}
              title="Refresh Users"
            >
              <span style={{ fontSize: '10px', color: 'var(--primary-500)' }}>REFRESH</span>
            </button>
            <span className="badge badge-primary">{users.length}</span>
          </div>
        </div>

        {/* Contacts List */}
        <div style={{ flex: 1, overflowY: 'auto', padding: 'var(--space-2)' }}>
          {loading ? (
            <div style={{ padding: 'var(--space-2)', display: 'flex', flexDirection: 'column', gap: 'var(--space-2)' }}>
              {[1, 2, 3, 4, 5].map((i) => (
                <div key={i} className="skeleton" style={{ height: '64px', borderRadius: 'var(--radius-md)' }}></div>
              ))}
            </div>
          ) : users.length === 0 ? (
            <div style={{ textAlign: 'center', padding: 'var(--space-12) var(--space-4)' }}>
              <User size={48} style={{ margin: '0 auto var(--space-4)', opacity: 0.3, color: 'var(--text-tertiary)' }} />
              <p style={{ fontSize: '14px', color: 'var(--text-tertiary)', marginBottom: 'var(--space-2)' }}>No users found</p>
              <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                Register a new account in a different browser to see them here.
              </p>
            </div>
          ) : (
            <AnimatePresence>
              {users.map((u, index) => {
                const isActive = currentChatUser?.id === u.id;
                return (
                  <motion.button
                    key={u.id}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: index * 0.03 }}
                    onClick={() => onSelectUser(u)}
                    style={{
                      width: '100%',
                      display: 'flex',
                      alignItems: 'center',
                      gap: 'var(--space-3)',
                      padding: 'var(--space-3)',
                      marginBottom: 'var(--space-1)',
                      borderRadius: 'var(--radius-md)',
                      background: isActive ? 'var(--bg-active)' : 'transparent',
                      border: 'none',
                      cursor: 'pointer',
                      transition: 'all 0.2s',
                      textAlign: 'left',
                    }}
                    className={!isActive ? 'hover-bg' : ''}
                    onMouseEnter={(e) => {
                      if (!isActive) e.currentTarget.style.background = 'var(--bg-secondary)';
                    }}
                    onMouseLeave={(e) => {
                      if (!isActive) e.currentTarget.style.background = 'transparent';
                    }}
                  >
                    {/* Avatar */}
                    <div style={{ position: 'relative' }}>
                      <div className="avatar avatar-lg" style={{ background: isActive ? 'white' : 'linear-gradient(135deg, var(--primary-500), var(--primary-700))' }}>
                        <span style={{ color: isActive ? 'var(--primary-600)' : 'white' }}>
                          {u.username?.[0]?.toUpperCase() || 'U'}
                        </span>
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

                    {/* User Info */}
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <h3
                        className="font-medium truncate"
                        style={{
                          fontSize: '15px',
                          color: isActive ? 'white' : 'var(--text-primary)',
                          marginBottom: '2px',
                        }}
                      >
                        {u.username}
                      </h3>
                      <p
                        className="text-sm truncate"
                        style={{
                          color: isActive ? 'rgba(255,255,255,0.8)' : 'var(--text-tertiary)',
                        }}
                      >
                        {u.firstName && u.lastName
                          ? `${u.firstName} ${u.lastName}`
                          : 'Click to chat'}
                      </p>
                    </div>

                    {/* Active Indicator */}
                    {isActive && (
                      <div
                        style={{
                          width: '8px',
                          height: '8px',
                          borderRadius: 'var(--radius-full)',
                          background: 'white',
                        }}
                      ></div>
                    )}
                  </motion.button>
                );
              })}
            </AnimatePresence>
          )}
        </div>
      </aside>
    </>
  );
}
