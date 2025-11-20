// src/pages/HomePage.jsx
import { useAuthContext } from "../hooks/useAuthContext";
import { useChatLogic } from "../hooks/useChatLogic";
import Sidebar from "../components/Sidebar";
import ChatHeader from "../components/ChatHeader";
import MessageList from "../components/MessageList";
import MessageInput from "../components/MessageInput";
import LoadingIndicator from "../components/LoadingIndicator";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";

const HomePage = () => {
  const { user, isAuthenticated, logout, initializing } = useAuthContext();

  const {
    filteredUsers,
    loadingUsers,
    search,
    setSearch,
    startChat,
    currentChatUser,
    messages,
    sendMessage,
    typingUser,
  } = useChatLogic({ user, token: user?.token });

  const navigate = useNavigate();

  useEffect(() => {
    if (!initializing && !isAuthenticated) {
      navigate("/auth");
    }
  }, [initializing, isAuthenticated, navigate]);

  if (initializing) {
    return <LoadingIndicator />;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-slate-950 to-indigo-900 text-white flex flex-col md:flex-row">
      {/* Sidebar (top on mobile, left on desktop) */}
      <Sidebar
        user={user}
        users={filteredUsers}
        loading={loadingUsers}
        search={search}
        setSearch={setSearch}
        onSelectUser={startChat}
        logout={logout}
        currentChatUser={currentChatUser}
      />

      {/* Chat area */}
      <main className="flex-1 flex flex-col">
        <ChatHeader chatUser={currentChatUser} />

        <section className="flex-1 flex flex-col md:p-4 lg:p-6">
          <div className="flex-1 bg-white/5 md:bg-white/10 rounded-none md:rounded-3xl shadow-none md:shadow-2xl backdrop-blur-sm overflow-hidden border-t md:border border-white/10">
            {currentChatUser ? (
              <MessageList
                messages={messages}
                currentUser={user}
                typingUser={typingUser}
              />
            ) : (
              <div className="flex flex-col justify-center items-center h-full opacity-80 select-none px-4 text-center">
                <h2 className="text-2xl md:text-3xl font-semibold mb-2">
                  Welcome to MaitriConnect 🎉
                </h2>
                <p className="mt-1 text-white/70 text-sm md:text-base max-w-md">
                  Select a user from the sidebar to start a private conversation.
                </p>
              </div>
            )}
          </div>

          {currentChatUser && (
            <div className="border-t border-white/10 bg-slate-900/80 md:bg-transparent md:border-none mt-0 md:mt-3">
              <MessageInput onSend={sendMessage} />
            </div>
          )}
        </section>
      </main>
    </div>
  );
};

export default HomePage;
