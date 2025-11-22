import { useState, useEffect, useRef } from "react";
import axios from "axios";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { fileService } from "../services/fileService";

const API_BASE = "http://localhost:8080"; // REST API through gateway
const WS_BASE = "http://localhost:8082"; // WebSocket direct to chat-service

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
  const [isConnected, setIsConnected] = useState(false); // Track connection state

  const stompClientRef = useRef(null);
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
        console.log('👥 Fetched users:', res.data.length);
        setAllUsers(res.data);
        setLoadingUsers(false);
      })
      .catch((err) => {
        console.error('❌ Failed to fetch users:', err);
        setLoadingUsers(false);
      });
  }, [token]);

  // ---- STOMP WebSocket connection ----
  useEffect(() => {
    if (!token || !user) return;

    console.log('🔌 Initializing STOMP connection to chat-service...');

    const client = new Client({
      webSocketFactory: () => new SockJS(`${WS_BASE}/ws`), // Direct to chat-service
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      onConnect: () => {
        console.log('✅ STOMP Connected successfully');
        setIsConnected(true);
      },
      onDisconnect: () => {
        console.log('❌ STOMP Disconnected');
        setIsConnected(false);
      },
      onStompError: (frame) => {
        console.error('❌ STOMP error:', frame);
        setIsConnected(false);
      },
    });

    client.activate();
    stompClientRef.current = client;

    return () => {
      console.log('🔌 Cleaning up STOMP connection');
      if (roomSubscriptionRef.current) {
        roomSubscriptionRef.current.unsubscribe();
      }
      client.deactivate();
      setIsConnected(false);
    };
  }, [token, user]);

  // ---- Subscribe to current room when connection OR room changes ----
  useEffect(() => {
    // Wait for both connection and room to be ready
    if (!isConnected || !currentRoomId || !stompClientRef.current) {
      console.log('⏳ Waiting for connection or room...', { isConnected, currentRoomId });
      return;
    }

    console.log(`🔔 Subscribing to room: ${currentRoomId}`);

    // Unsubscribe from previous room
    if (roomSubscriptionRef.current) {
      console.log('🔕 Unsubscribing from previous room');
      roomSubscriptionRef.current.unsubscribe();
      roomSubscriptionRef.current = null;
    }

    // Subscribe to new room
    try {
      const subscription = stompClientRef.current.subscribe(
        `/topic/room.${currentRoomId}`,
        (message) => {
          const msg = JSON.parse(message.body);
          console.log('📨 Received WebSocket message:', msg);

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
            console.log('💬 Adding new message to state');

            // Fetch file metadata if message has file attachments
            if (msg.fileAttachments && msg.fileAttachments.length > 0) {
              msg.fileAttachments.forEach(async (fileId) => {
                // Only fetch if we don't already have it
                if (!fileAttachments[fileId]) {
                  try {
                    const metadata = await fileService.getFileMetadata(fileId, token);
                    setFileAttachments((prev) => ({
                      ...prev,
                      [fileId]: metadata,
                    }));
                    console.log('📎 Fetched file metadata for:', fileId);
                  } catch (error) {
                    console.error('Failed to fetch file metadata:', error);
                  }
                }
              });
            }

            setMessages((prev) => {
              // Prevent duplicates
              const exists = prev.some(m => m.id === msg.id);
              if (exists) {
                console.log('⚠️ Message already exists, skipping');
                return prev;
              }
              return [...prev, msg];
            });
          }

          // Handle join/leave messages
          if (msg.type === "JOIN" || msg.type === "LEAVE") {
            setMessages((prev) => [...prev, msg]);
          }
        }
      );

      roomSubscriptionRef.current = subscription;
      console.log('✅ Successfully subscribed to room:', currentRoomId);

    } catch (error) {
      console.error('❌ Failed to subscribe to room:', error);
    }

    return () => {
      if (roomSubscriptionRef.current) {
        console.log('🔕 Cleaning up room subscription');
        roomSubscriptionRef.current.unsubscribe();
        roomSubscriptionRef.current = null;
      }
    };
  }, [isConnected, currentRoomId, user]); // Added isConnected to dependencies

  // ---- Selecting a user / opening a chat ----
  const startChat = async (chatUser) => {
    if (!user || !token) return;

    console.log('💬 Starting chat with:', chatUser.username);
    setCurrentChatUser(chatUser);

    // 1. Get or create room from backend
    const roomRes = await axios.get(
      `${API_BASE}/api/chat/rooms/direct?user1=${user.userId}&user2=${chatUser.id}`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    const room = roomRes.data;
    console.log('📦 Got room:', room.id);
    setCurrentRoomId(room.id);

    // 2. Load historical messages once
    const msgsRes = await axios.get(
      `${API_BASE}/api/chat/messages/direct?user1=${user.userId}&user2=${chatUser.id}`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    console.log('📜 Loaded', msgsRes.data.length, 'historical messages');

    const historicalMessages = msgsRes.data;
    setMessages(historicalMessages);

    // Fetch metadata for any files in historical messages
    historicalMessages.forEach(msg => {
      if (msg.fileAttachments && msg.fileAttachments.length > 0) {
        msg.fileAttachments.forEach(async (fileId) => {
          if (!fileAttachments[fileId]) {
            try {
              const metadata = await fileService.getFileMetadata(fileId, token);
              setFileAttachments((prev) => ({
                ...prev,
                [fileId]: metadata,
              }));
            } catch (error) {
              console.error('Failed to fetch historical file metadata:', error);
            }
          }
        });
      }
    });
  };

  // ---- Sending a message ----
  const sendMessage = async (content) => {
    if (!content.trim() && !selectedFile) return;
    if (!currentRoomId || !user || !currentChatUser) return;

    console.log('📤 Sending message...');

    let fileId = null;

    // Upload file if selected
    if (selectedFile) {
      try {
        const fileResponse = await fileService.uploadFile(
          selectedFile,
          user.userId,
          currentRoomId,
          (progress) => console.log(`Upload progress: ${progress}%`),
          token
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

    // Clear selected file
    setSelectedFile(null);

    // Logic: 
    // 1. If connected via WebSocket, send via STOMP. The server will save and broadcast it back.
    //    We do NOT add it to state here, we wait for the broadcast to avoid duplicates.
    // 2. If NOT connected, send via REST API and manually add to state.

    if (stompClientRef.current && isConnected) {
      try {
        console.log('📡 Publishing message via STOMP');
        stompClientRef.current.publish({
          destination: '/app/chat.sendMessage',
          body: JSON.stringify(msg),
        });
      } catch (error) {
        console.error('❌ Failed to send message via STOMP:', error);
        alert("Failed to send message. Please try again.");
      }
    } else {
      console.warn('⚠️ STOMP not connected, falling back to REST API');

      // Fallback: Persist via REST API
      try {
        const res = await axios.post(
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
        console.log('✅ Message saved via REST');
        // Manually add to state since we won't get a WebSocket broadcast
        setMessages((prev) => [...prev, res.data]);
      } catch (error) {
        console.error('❌ Failed to save message via REST:', error);
        alert("Failed to send message. Please check your connection.");
      }
    }
  };

  // ---- Typing indicators with debouncing ----
  const sendTypingIndicator = () => {
    if (!currentRoomId || !user || !isConnected) return;

    // Clear existing timeout
    if (typingTimeoutRef.current) {
      clearTimeout(typingTimeoutRef.current);
    }

    // Send typing event
    if (stompClientRef.current) {
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
      if (stompClientRef.current && isConnected) {
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
    if (!user || !stompClientRef.current || !isConnected) return;

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
    socketConnected: isConnected,
  };
}
