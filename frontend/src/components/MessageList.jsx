function formatTime(ts) {
  if (!ts) return "";
  const date = new Date(ts);
  return date.toLocaleTimeString("en-US", {
    hour: "2-digit",
    minute: "2-digit",
    hour12: true,
  });
}

export default function MessageList({ messages, currentUser, typingUser }) {
  return (
    <div className="flex flex-col gap-2 px-3 py-4 md:px-6 md:py-6 overflow-y-auto h-full">
      {messages.length === 0 && (
        <div className="flex flex-col justify-center items-center flex-1 opacity-70 select-none">
          <h3 className="text-xl md:text-2xl font-bold mb-1">
            No messages yet
          </h3>
          <p className="mt-1 text-white/70 text-sm md:text-base">
            Start the conversation by sending a message!
          </p>
        </div>
      )}

      {messages.map((msg, i) => {
        if (msg.type === "JOIN" || msg.type === "LEAVE") {
          return (
            <div key={i} className="flex justify-center my-1">
              <span className="text-[11px] md:text-xs bg-white/15 px-3 py-1 rounded-full text-white/80">
                {msg.senderUsername || msg.senderId}{" "}
                {msg.type === "JOIN" ? "joined" : "left"} the chat
              </span>
            </div>
          );
        }

        const isMine =
          String(msg.senderId) === String(currentUser?.userId) ||
          msg.senderUsername === currentUser?.username;

        return (
          <div
            key={i}
            className={`flex w-full mb-1 ${
              isMine ? "justify-end" : "justify-start"
            }`}
          >
            <div
              className={`max-w-[80%] md:max-w-md px-3 py-2 rounded-2xl shadow-sm text-sm md:text-base ${
                isMine
                  ? "bg-indigo-500 text-white rounded-br-none"
                  : "bg-slate-800 text-white rounded-bl-none"
              }`}
            >
              <div className="whitespace-pre-wrap break-words">
                {msg.content}
              </div>
              <div className="text-[10px] text-white/60 mt-1 text-right">
                {formatTime(msg.timestamp)}
              </div>
            </div>
          </div>
        );
      })}

      {typingUser && (
        <div className="mt-2 text-xs md:text-sm text-white/70 italic">
          {typingUser} is typing…
        </div>
      )}
    </div>
  );
}
