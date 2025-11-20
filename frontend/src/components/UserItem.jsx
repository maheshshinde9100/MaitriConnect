export default function UserItem({ user, selected, onClick }) {
  return (
    <div
      className={`cursor-pointer flex items-center gap-3 px-5 py-3 hover:bg-indigo-800/30 ${selected ? "bg-indigo-800/50" : ""}`}
      onClick={onClick}
    >
      <div className="w-10 h-10 rounded-full flex items-center justify-center text-lg font-semibold bg-indigo-500 text-white relative">
        {(user.firstName ? user.firstName[0] : user.username[0]).toUpperCase()}
        <span className={`absolute bottom-1 right-1 w-2 h-2 rounded-full border-2 border-slate-900 ${user.online ? "bg-emerald-400" : "bg-gray-400"}`}></span>
      </div>
      <div className="flex flex-col flex-1 overflow-hidden">
        <span className="truncate font-medium text-white">{user.firstName && user.lastName ? `${user.firstName} ${user.lastName}` : user.username}</span>
        <span className="truncate text-xs text-white/50">{user.status || "Hey there! I'm using MaitriConnect"}</span>
      </div>
    </div>
  );
}
