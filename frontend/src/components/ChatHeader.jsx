export default function ChatHeader({ chatUser }) {
  return (
    <div className="h-20 px-6 flex items-center border-b border-white/10 bg-slate-900/90 transition">
      {chatUser ? (
        <div className="flex items-center gap-4">
          <div className="w-11 h-11 rounded-full flex items-center justify-center text-lg font-semibold bg-indigo-500 text-white">
            {(chatUser.firstName ? chatUser.firstName[0] : chatUser.username[0]).toUpperCase()}
          </div>
          <div>
            <div className="font-semibold text-lg text-white">{chatUser.firstName && chatUser.lastName ? `${chatUser.firstName} ${chatUser.lastName}` : chatUser.username}</div>
            <div className={`text-sm ${chatUser.online ? "text-emerald-400" : "text-white/60"}`}>
              {chatUser.online ? "Online" : "Offline"}
            </div>
          </div>
        </div>
      ) : (
        <h3 className="text-2xl font-semibold text-white/80">Select a user to start chatting</h3>
      )}
    </div>
  );
}
