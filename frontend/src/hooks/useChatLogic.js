import { useState, useEffect, useRef } from "react";
import axios from "axios";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { fileService } from "../services/fileService";

const API_BASE = "http://localhost:8080";

export function useChatLogic({ user, token }) {
  const [allUsers, setAllUsers] = useState([]);
  const [loadingUsers, setLoadingUsers] = useState(true);
  const [search, setSearch] = useState("");
  const [messages, setMessages] = useState([]);
  const [currentChatUser, setCurrentChatUser] = useState(null);
  const [currentRoomId, setCurrentRoomId] = useState(null);
  const [typingUser, setTypingUser] = useState(null);
  const [fileAttachments, setFileAttachments] = useState({});
  const [selectedFile, setSelectedFile] = useState(null);

  const stompClientRef = useRef(null);
  const [connecting, setConnecting] = useState(false);
  const typingTimeoutRef = useRef(null);
  const roomSubscriptionRef = useRef(null);

  // ---- Load all users once authenticated ----
  useEffect(() => {
    if (!token) return;
    setLoadingUsers(true);

    axios
      .get(`${API_BASE}/api/auth/users`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setAllUsers(res.data);
        setLoadingUsers(false);
      })
      .catch(() => setLoadingUsers(false));
  }, [token]);

  // ---- STOMP WebSocket connection ----
  useEffect(() => {
    if (!token || !user) return;

    setConnecting(true);

    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_BASE}/ws`),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('✅ STOMP Connected');
        setConnecting(false);
      },
      onDisconnect: () => {
        console.log('❌ STOMP Disconnected');
        setConnecting(false);
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
        setConnecting(false);
      },
    });

    client.activate();
    stompClientRef.current = client;

    return () => {
      if (roomSubscriptionRef.current) {
        roomSubscriptionRef.current.unsubscribe();
      }
      client.deactivate();
    };
  }, [token, user]);

  // ---- Subscribe to current room when it changes ----
  useEffect(() => {
    if (!stompClientRef.current || !currentRoomId || !stompClientRef.current.connected) {
      return;
    }

    // Unsubscribe from previous room
    if (roomSubscriptionRef.current) {
      roomSubscriptionRef.current.unsubscribe();
    }

    // Subscribe to new room
    const subscription = stompClientRef.current.subscribe(
      `/topic/room.${currentRoomId}`,
      (message) => {
        const msg = JSON.parse(message.body);
        console.log('📨 Received message:', msg);

        // Handle typing indicators
        if (msg.type === "TYPING") {
          setTypingUser(msg.senderId);
          return;
        }
        if (msg.type === "STOP_TYPING") {
          setTypingUser(null);
          return;
        }

        // Handle reactions
        if (msg.type === "REACTION") {
          setMessages((prev) =>
            prev.map((m) =>
              m.id === msg.messageId
                ? { ...m, reactions: msg.reactions || {} }
                : m
            )
          );
          return;
        }

        // Handle delivered/read receipts
        if (msg.type === "DELIVERED" || msg.type === "READ") {
          setMessages((prev) =>
            prev.map((m) => {
              if (m.chatRoomId === msg.chatRoomId && m.senderId === user.userId) {
                return {
                  ...m,
                  status: msg.type,
                  deliveredAt: msg.type === "DELIVERED" ? new Date().toISOString() : m.deliveredAt,
                  readAt: msg.type === "READ" ? new Date().toISOString() : m.readAt,
                };
              }
              return m;
            })
          );
          return;
        }

        // Handle chat messages
        if (msg.type === "CHAT" || msg.type === "FILE") {
          setMessages((prev) => [...prev, msg]);
        }

        // Handle join/leave messages
        if (msg.type === "JOIN" || msg.type === "LEAVE") {
          setMessages((prev) => [...prev, msg]);
        }
      }
    );

    roomSubscriptionRef.current = subscription;

    return () => {
      if (subscription) {
        subscription.unsubscribe();
      }
    };
  }, [currentRoomId, user]);

  // ---- Selecting a user / opening a chat ----
  const startChat = async (chatUser) => {
    if (!user || !token) return;

    setCurrentChatUser(chatUser);

    // 1. Get or create room from backend
    const roomRes = await axios.get(
      `${API_BASE}/api/chat/rooms/direct?user1=${user.userId}&user2=${chatUser.id}`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    const room = roomRes.data;
    setCurrentRoomId(room.id);

    // 2. Load historical messages once
    const msgsRes = await axios.get(
      `${API_BASE}/api/chat/messages/direct?user1=${user.userId}&user2=${chatUser.id}`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    setMessages(msgsRes.data);
  };

  // ---- Sending a message ----
  const sendMessage = async (content) => {
    if (!content.trim() && !selectedFile) return;
    if (!currentRoomId || !user || !currentChatUser) return;

    let fileId = null;

    // Upload file if selected
    if (selectedFile) {
      try {
        const fileResponse = await fileService.uploadFile(
          selectedFile,
          user.userId,
          currentRoomId,
          (progress) => console.log(`Upload progress: ${progress}%`)
        );
        fileId = fileResponse.fileId;

        // Store file metadata
        setFileAttachments((prev) => ({
          ...prev,
          [fileId]: fileResponse,
        }));
      } catch (error) {
        console.error("File upload failed:", error);
        alert("Failed to upload file");
        return;
      }
    }

    const msg = {
      senderId: user.userId,
      senderUsername: user.username,
      receiverId: currentChatUser.id,
      content: content.trim(),
      chatRoomId: currentRoomId,
      type: fileId ? "FILE" : "CHAT",
      timestamp: new Date().toISOString(),
      fileAttachments: fileId ? [fileId] : [],
      status: "SENT",
    };

    // Immediately show on sender UI
    setMessages((prev) => [...prev, msg]);

    // Clear selected file
    setSelectedFile(null);

    // Send via STOMP so receiver sees it instantly
    if (stompClientRef.current && stompClientRef.current.connected) {
      try {
        stompClientRef.current.publish({
          destination: '/app/chat.sendMessage',
          body: JSON.stringify(msg),
        });
      } catch (error) {
        console.error('Failed to send message via STOMP:', error);
      }
    }

    // Also persist via REST API to database
    try {
      await axios.post(
        `${API_BASE}/api/chat/messages`,
        {
          senderId: user.userId,
          receiverId: currentChatUser.id,
          content: content.trim(),
          chatRoomId: currentRoomId,
          type: fileId ? "FILE" : "CHAT",
          fileAttachments: fileId ? [fileId] : [],
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
    } catch (error) {
      console.error('Failed to save message to database:', error);
    }
  };

  // ---- Typing indicators with debouncing ----
  const sendTypingIndicator = () => {
    if (!currentRoomId || !user) return;

    // Clear existing timeout
    if (typingTimeoutRef.current) {
      clearTimeout(typingTimeoutRef.current);
    }

    // Send typing event
    if (stompClientRef.current && stompClientRef.current.connected) {
      stompClientRef.current.publish({
        destination: '/app/chat.typing',
        body: JSON.stringify({
          senderId: user.userId,
          chatRoomId: currentRoomId,
          type: "TYPING",
        }),
      });
    }

    // Set timeout to send stop typing after 3 seconds
    typingTimeoutRef.current = setTimeout(() => {
      if (stompClientRef.current && stompClientRef.current.connected) {
        stompClientRef.current.publish({
          destination: '/app/chat.stopTyping',
          body: JSON.stringify({
            senderId: user.userId,
            chatRoomId: currentRoomId,
            type: "STOP_TYPING",
          }),
        });
      }
    }, 3000);
  };

  // ---- Reactions ----
  const handleReaction = (messageId, emoji) => {
    if (!user || !stompClientRef.current) return;

    if (stompClientRef.current.connected) {
      stompClientRef.current.publish({
        destination: '/app/chat.reaction',
        body: JSON.stringify({
          type: "REACTION",
          messageId,
          userId: user.userId,
          username: user.username,
          emoji,
          action: "toggle",
        }),
      });
    }
  };

  // ---- User search / filtering ----
  const filteredUsers = allUsers
    .filter((u) => String(u.id) !== String(user?.userId))
    .filter((u) => {
      const term = search.trim().toLowerCase();
      if (!term) return true;
      return (
        u.username.toLowerCase().includes(term) ||
        (u.firstName && u.firstName.toLowerCase().includes(term)) ||
        (u.lastName && u.lastName.toLowerCase().includes(term))
      );
    });

  return {
    allUsers,
    loadingUsers,
    filteredUsers,
    search,
    setSearch,
    messages,
    sendMessage,
    startChat,
    currentChatUser,
    currentRoomId,
    user,
    typingUser,
    sendTypingIndicator,
    handleReaction,
    fileAttachments,
    selectedFile,
    setSelectedFile,
    socketConnected:
      !connecting &&
      !!stompClientRef.current &&
      stompClientRef.current.connected,
  };
}
