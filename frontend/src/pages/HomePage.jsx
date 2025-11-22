import { useAuthContext } from "../hooks/useAuthContext";
import { useChatLogic } from "../hooks/useChatLogic";
import Sidebar from "../components/Sidebar";
import ChatHeader from "../components/ChatHeader";
import MessageList from "../components/MessageList";
import MessageInput from "../components/MessageInput";
import LoadingIndicator from "../components/LoadingIndicator";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { MessageCircle, Menu } from "lucide-react";

const HomePage = () => {
  const { user, isAuthenticated, logout, initializing } = useAuthContext();
  const [isMobile, setIsMobile] = useState(window.innerWidth < 768);
  const [isSidebarOpen, setIsSidebarOpen] = useState(!isMobile);

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
    const handleResize = () => {
      const mobile = window.innerWidth < 768;
      setIsMobile(mobile);
      if (!mobile) setIsSidebarOpen(true);
      else if (mobile && isSidebarOpen) setIsSidebarOpen(false);
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  useEffect(() => {
    if (!initializing && !isAuthenticated) {
      navigate("/auth");
    }
  }, [initializing, isAuthenticated, navigate]);

  const handleSelectUser = (u) => {
    startChat(u);
    if (isMobile) setIsSidebarOpen(false);
  };

  if (initializing) {
    return <LoadingIndicator />;
  }

  return (
    <div style={{
      display: 'flex',
      height: '100vh',
      overflow: 'hidden',
      background: 'var(--bg-app)',
      position: 'relative'
    }}>
      {/* Sidebar */}
      <Sidebar
        user={user}
        users={filteredUsers}
        loading={loadingUsers}
        search={search}
        setSearch={setSearch}
        onSelectUser={handleSelectUser}
        logout={logout}
        currentChatUser={currentChatUser}
        isMobile={isMobile}
        isOpen={isSidebarOpen}
        onClose={() => setIsSidebarOpen(false)}
      />

      {/* Main Chat Area */}
      <main style={{ flex: 1, display: 'flex', flexDirection: 'column', position: 'relative' }}>
        {/* Mobile Header / Menu Button */}
        {isMobile && !isSidebarOpen && (
          <div style={{
            padding: 'var(--space-3)',
            background: 'var(--bg-primary)',
            borderBottom: '1px solid var(--border-primary)',
            display: 'flex',
            alignItems: 'center',
            gap: 'var(--space-3)'
          }}>
            <button
              onClick={() => setIsSidebarOpen(true)}
              className="btn-ghost"
              style={{ padding: 'var(--space-2)', borderRadius: 'var(--radius-md)' }}
            >
              <Menu size={24} style={{ color: 'var(--text-primary)' }} />
            </button>
            <span className="font-semibold text-lg">MaitriConnect</span>
          </div>
        )}

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
