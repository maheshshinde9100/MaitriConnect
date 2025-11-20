// src/components/Sidebar.jsx
import UserItem from "./UserItem";
import { LogOut, Search } from "lucide-react";

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
    <aside className="w-full md:w-72 lg:w-80 h-[40vh] md:h-screen bg-slate-900/90 border-b md:border-b-0 md:border-r border-white/10 flex flex-col relative z-30">
      {/* Top user bar */}
      <div className="p-4 flex items-center gap-3 border-b border-white/10">
        <div className="w-10 h-10 md:w-11 md:h-11 ring-2 ring-indigo-400 rounded-full flex items-center justify-center text-lg md:text-xl font-semibold bg-indigo-600 text-white">
          {user?.username?.charAt(0).toUpperCase()}
        </div>
        <div className="flex-1 min-w-0">
          <div className="font-semibold text-white truncate">
            {user?.username}
          </div>
          <div className="text-[11px] text-emerald-400">Online</div>
        </div>
        <button
          className="ml-auto p-2 rounded-full hover:bg-slate-800/70 text-white/80"
          onClick={logout}
          title="Logout"
        >
          <LogOut className="w-4 h-4" />
        </button>
      </div>

      {/* Search */}
      <div className="px-4 py-2">
        <div className="relative flex items-center">
          <input
            className="w-full rounded-lg py-2 pl-9 pr-3 bg-slate-800/70 text-white placeholder:text-white/50 border border-white/10 focus:border-indigo-500 focus:outline-none text-sm"
            placeholder="Search users…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <Search className="absolute left-2.5 top-2.5 w-4 h-4 text-white/50" />
        </div>
      </div>

      {/* User list */}
      <div className="flex-1 overflow-y-auto pb-2">
        {loading ? (
          <div className="text-center mt-6 text-white/60 text-sm">
            Loading users…
          </div>
        ) : users.length ? (
          users.map((u) => (
            <UserItem
              key={u.id}
              user={u}
              selected={currentChatUser && u.id === currentChatUser.id}
              onClick={() => onSelectUser(u)}
            />
          ))
        ) : (
          <div className="text-center mt-6 text-white/60 text-sm">
            No other users found
          </div>
        )}
      </div>
    </aside>
  );
}
