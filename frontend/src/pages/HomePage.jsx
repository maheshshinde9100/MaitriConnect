import { useAuthContext } from "../hooks/useAuthContext";
import { useChatLogic } from "../hooks/useChatLogic";
import Sidebar from "../components/Sidebar";
import ChatHeader from "../components/ChatHeader";
import MessageList from "../components/MessageList";
import MessageInput from "../components/MessageInput";
import LoadingIndicator from "../components/LoadingIndicator";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import { MessageCircle } from "lucide-react";

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
    <div style={{
      display: 'flex',
      height: '100vh',
      overflow: 'hidden',
      background: 'var(--bg-app)'
    }}>
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
      <main style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        {currentChatUser ? (
          <>
            <ChatHeader chatUser={currentChatUser} />

            <MessageList
              messages={messages}
              currentUser={user}
              typingUser={typingUser}
              onReact={handleReaction}
              fileAttachments={fileAttachments}
            />

            <MessageInput
              onSend={sendMessage}
              onFileSelect={setSelectedFile}
              selectedFile={selectedFile}
              onClearFile={() => setSelectedFile(null)}
            />
          </>
        ) : (
          /* Empty State */
          <div style={{
            flex: 1,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            background: 'var(--bg-app)',
          }}>
            <div style={{ textAlign: 'center', maxWidth: '400px', padding: 'var(--space-6)' }} className="animate-fade-in">
              <div style={{
                width: '120px',
                height: '120px',
                margin: '0 auto var(--space-6)',
                background: 'linear-gradient(135deg, var(--primary-600), var(--primary-700))',
                borderRadius: 'var(--radius-xl)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: '0 20px 40px rgba(107, 110, 245, 0.3)',
              }}>
                <MessageCircle size={60} style={{ color: 'white' }} />
              </div>

              <h1 className="font-bold text-3xl" style={{
                color: 'var(--text-primary)',
                marginBottom: 'var(--space-3)',
              }}>
                Welcome to MaitriConnect
              </h1>

              <p style={{
                fontSize: '16px',
                color: 'var(--text-secondary)',
                marginBottom: 'var(--space-2)',
                lineHeight: 1.6,
              }}>
                Start a conversation by selecting a contact from the sidebar
              </p>

              <p style={{
                fontSize: '14px',
                color: 'var(--text-tertiary)',
              }}>
                Real-time messaging • File sharing • Reactions
              </p>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default HomePage;
