import { useState, useEffect, useRef } from "react";
import axios from "axios";

const API_BASE = "http://localhost:8080";
const WS_URL = "ws://localhost:8080/ws";

export function useChatLogic({ user, token }) {
  const [allUsers, setAllUsers] = useState([]);
  const [loadingUsers, setLoadingUsers] = useState(true);
  const [search, setSearch] = useState("");
  const [messages, setMessages] = useState([]);
  const [currentChatUser, setCurrentChatUser] = useState(null);
  const [currentRoomId, setCurrentRoomId] = useState(null);
  const [typingUser, setTypingUser] = useState(null);

  const socketRef = useRef(null);
  const [connecting, setConnecting] = useState(false);

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

  // ---- WebSocket connection (single, long‑lived) ----
  useEffect(() => {
    if (!token || !user) return;

    setConnecting(true);
    const ws = new WebSocket(WS_URL);
    socketRef.current = ws;

    ws.onopen = () => {
      setConnecting(false);

      // Optional: send a SUBSCRIBE message for this userId if your backend expects it
      try {
        ws.send(
          JSON.stringify({
            type: "SUBSCRIBE",
            userId: user.userId,
          })
        );
      } catch {
        // ignore
      }
    };

    ws.onclose = () => {
      setConnecting(false);
    };

    ws.onerror = () => {
      setConnecting(false);
    };

    // IMPORTANT: this handler does NOT depend on currentRoomId,
    // it runs for the lifetime of the socket
    ws.onmessage = (event) => {
      let msg;
      try {
        msg = JSON.parse(event.data);
      } catch {
        return;
      }

      // typing indicators
      if (msg.type === "TYPING") {
        setTypingUser(msg.senderId);
        return;
      }
      if (msg.type === "STOP_TYPING") {
        setTypingUser(null);
        return;
      }

      // Only append messages that are relevant:
      // 1) belong to the currently open room, OR
      // 2) direct to this user (so if sender initiates a new room, you still see it)
      const isForCurrentRoom =
        msg.chatRoomId && currentRoomId && msg.chatRoomId === currentRoomId;

      const isDirectToMe =
        msg.receiverId &&
        String(msg.receiverId) === String(user.userId) &&
        (!currentRoomId || msg.chatRoomId === currentRoomId);

      if (msg.type === "CHAT" || msg.type === "JOIN" || msg.type === "LEAVE") {
        if (isForCurrentRoom || isDirectToMe) {
          setMessages((prev) => [...prev, msg]);
        }
      }
    };

    return () => {
      ws.close();
    };
  }, [token, user, currentRoomId]); // currentRoomId is here only so `isForCurrentRoom` can be checked with latest value

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

    // 3. Announce JOIN to the room over WebSocket (optional)
    if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      socketRef.current.send(
        JSON.stringify({
          senderId: user.userId,
          senderUsername: user.username,
          content: "",
          chatRoomId: room.id,
          type: "JOIN",
          timestamp: new Date().toISOString(),
        })
      );
    }
  };

  // ---- Sending a message ----
  const sendMessage = async (content) => {
    if (!content.trim() || !currentRoomId || !user || !currentChatUser) return;

    const msg = {
      senderId: user.userId,
      senderUsername: user.username,
      receiverId: currentChatUser.id,
      content,
      chatRoomId: currentRoomId,
      type: "CHAT",
      timestamp: new Date().toISOString(),
    };

    // Immediately show on sender UI
    setMessages((prev) => [...prev, msg]);

    // Send via WS so receiver sees it instantly
    if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      try {
        socketRef.current.send(JSON.stringify(msg));
      } catch {
        // ignore
      }
    }

    // Persist via REST – no need to push to state again
    try {
      await axios.post(
        `${API_BASE}/api/chat/messages`,
        {
          senderId: user.userId,
          receiverId: currentChatUser.id,
          content,
          chatRoomId: currentRoomId,
          type: "CHAT",
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
    } catch {
      // optional: show toast
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
    socketConnected:
      !connecting &&
      !!socketRef.current &&
      socketRef.current.readyState === WebSocket.OPEN,
  };
}
