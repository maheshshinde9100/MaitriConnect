import { Search, LogOut, Users, Settings } from "lucide-react";
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
}) {
  return (
    <aside
      className="w-80 flex flex-col border-r"
      style={{
        background: 'var(--bg-secondary)',
        borderColor: 'var(--border-color)',
      }}
    >
      {/* Sidebar Header */}
      <div
        className="p-4 border-b"
        style={{ borderColor: 'var(--border-color)' }}
      >
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full gradient-primary flex items-center justify-center font-bold text-white">
              {user?.username?.[0]?.toUpperCase() || 'U'}
            </div>
            <div>
              <h2 className="font-semibold text-base" style={{ color: 'var(--text-primary)' }}>
                {user?.username || 'User'}
              </h2>
              <p className="text-xs flex items-center gap-1" style={{ color: 'var(--text-tertiary)' }}>
                <span className="w-2 h-2 rounded-full status-online"></span>
                Online
              </p>
            </div>
          </div>
          <button
            onClick={logout}
            className="p-2 rounded-lg hover-lift"
            style={{ background: 'var(--bg-tertiary)' }}
            title="Logout"
          >
            <LogOut className="w-4 h-4" style={{ color: 'var(--text-secondary)' }} />
          </button>
        </div>

        {/* Search Bar */}
        <div className="relative">
          <Search
            className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4"
            style={{ color: 'var(--text-tertiary)' }}
          />
          <input
            type="text"
            placeholder="Search conversations..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full pl-10 pr-4 py-2.5 rounded-lg text-sm chat-input"
          />
        </div>
      </div>

      {/* Contacts Header */}
      <div className="px-4 py-3 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Users className="w-4 h-4" style={{ color: 'var(--text-secondary)' }} />
          <span className="text-sm font-semibold" style={{ color: 'var(--text-secondary)' }}>
            Contacts
          </span>
        </div>
        <span
          className="text-xs px-2 py-0.5 rounded-full"
          style={{ background: 'var(--bg-tertiary)', color: 'var(--text-tertiary)' }}
        >
          {users.length}
        </span>
      </div>

      {/* User List */}
      <div className="flex-1 overflow-y-auto px-2">
        {loading ? (
          <div className="space-y-2 p-2">
            {[1, 2, 3, 4, 5].map((i) => (
              <div key={i} className="skeleton h-16 rounded-xl"></div>
            ))}
          </div>
        ) : users.length === 0 ? (
          <div className="text-center py-12 px-4">
            <Users className="w-12 h-12 mx-auto mb-3 opacity-30" />
            <p className="text-sm" style={{ color: 'var(--text-tertiary)' }}>
              No contacts found
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
                  transition={{ delay: index * 0.05 }}
                  onClick={() => onSelectUser(u)}
                  className={`w-full p-3 rounded-xl mb-1 flex items-center gap-3 text-left transition-all ${isActive ? 'glass-strong' : 'hover:bg-opacity-50'
                    }`}
                  style={{
                    background: isActive ? 'var(--bg-active)' : 'transparent',
                  }}
                >
                  <div className="relative">
                    <div
                      className="w-12 h-12 rounded-full flex items-center justify-center font-semibold text-white"
                      style={{
                        background: isActive
                          ? 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
                          : 'var(--bg-tertiary)',
                      }}
                    >
                      {u.username?.[0]?.toUpperCase() || 'U'}
                    </div>
                    <span className="absolute bottom-0 right-0 w-3 h-3 rounded-full border-2 status-online"
                      style={{ borderColor: 'var(--bg-secondary)' }}
                    ></span>
                  </div>
                  <div className="flex-1 min-w-0">
                    <h3
                      className="font-medium text-sm truncate"
                      style={{ color: 'var(--text-primary)' }}
                    >
                      {u.username}
                    </h3>
                    <p
                      className="text-xs truncate"
                      style={{ color: 'var(--text-tertiary)' }}
                    >
                      {u.firstName && u.lastName
                        ? `${u.firstName} ${u.lastName}`
                        : 'Available'}
                    </p>
                  </div>
                  {isActive && (
                    <div className="w-2 h-2 rounded-full gradient-primary"></div>
                  )}
                </motion.button>
              );
            })}
          </AnimatePresence>
        )}
      </div>
    </aside>
  );
}
