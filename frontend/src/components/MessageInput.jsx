import { useState, useRef } from "react";
import { Send } from "lucide-react";

export default function MessageInput({ onSend }) {
  const [input, setInput] = useState("");
  const textareaRef = useRef();
  const handleInput = (e) => {
    setInput(e.target.value);
    textareaRef.current.style.height = "auto";
    textareaRef.current.style.height = textareaRef.current.scrollHeight + "px";
  };
  const handleSend = (e) => {
    e.preventDefault();
    if (input.trim() === "") return;
    onSend(input);
    setInput("");
    textareaRef.current.style.height = "auto";
  };
  return (
    <form className="flex items-center gap-3 px-6 py-4 bg-slate-900/70 border-t border-white/15" onSubmit={handleSend}>
      <textarea
        ref={textareaRef}
        className="flex-1 resize-none bg-slate-800/70 rounded-2xl px-4 py-2 text-white placeholder:text-white/50 min-h-[40px] max-h-[150px] focus:outline-none border border-white/10"
        placeholder="Type a message..."
        value={input}
        onChange={handleInput}
        rows={1}
        onKeyDown={e => {
          if (e.key === "Enter" && !e.shiftKey) handleSend(e);
        }}
      />
      <button type="submit" className="bg-indigo-500 hover:bg-indigo-600 text-white p-3 rounded-full transition">
        <Send className="w-5 h-5" />
      </button>
    </form>
  );
}
