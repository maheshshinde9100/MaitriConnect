import { useAuthContext } from "../hooks/useAuthContext";
import { useChatLogic } from "../hooks/useChatLogic";
import Sidebar from "../components/Sidebar";
import ChatHeader from "../components/ChatHeader";
import MessageList from "../components/MessageList";
import MessageInput from "../components/MessageInput";
import LoadingIndicator from "../components/LoadingIndicator";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import { MessageSquare } from "lucide-react";

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
    sendTypingIndicator,
    handleReaction,
    fileAttachments,
    selectedFile,
    setSelectedFile,
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
    <div className="flex h-screen overflow-hidden" style={{ background: 'var(--bg-primary)' }}>
      {/* Sidebar */}
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

      {/* Main Chat Area */}
      <main className="flex-1 flex flex-col relative">
        {currentChatUser ? (
          <>
            {/* Chat Header */}
            <ChatHeader chatUser={currentChatUser} />

            {/* Messages Container */}
            <div className="flex-1 overflow-hidden relative" style={{ background: 'var(--bg-secondary)' }}>
              <MessageList
                messages={messages}
                currentUser={user}
                typingUser={typingUser}
                onReact={handleReaction}
                fileAttachments={fileAttachments}
              />
            </div>

            {/* Message Input */}
            <div style={{ background: 'var(--bg-secondary)', borderTop: '1px solid var(--border-color)' }}>
              <MessageInput
                onSend={sendMessage}
                onFileSelect={setSelectedFile}
                selectedFile={selectedFile}
                onClearFile={() => setSelectedFile(null)}
              />
            </div>
          </>
        ) : (
          /* Empty State */
          <div className="flex-1 flex items-center justify-center" style={{ background: 'var(--bg-secondary)' }}>
            <div className="text-center px-8 animate-fade-in">
              <div className="inline-flex items-center justify-center w-24 h-24 rounded-full mb-6 gradient-primary">
                <MessageSquare className="w-12 h-12 text-white" />
              </div>
              <h2 className="text-3xl font-bold mb-3 gradient-text">
                Welcome to MaitriConnect
              </h2>
              <p className="text-lg mb-2" style={{ color: 'var(--text-secondary)' }}>
                Your conversations, elevated
              </p>
              <p className="text-sm" style={{ color: 'var(--text-tertiary)' }}>
                Select a contact from the sidebar to start chatting
              </p>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default HomePage;
