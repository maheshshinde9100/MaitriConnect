import { useCallback, useEffect, useMemo, useState } from 'react'
import { Filter, Loader2, Search } from 'lucide-react'
import toast from 'react-hot-toast'
import StoryStrip from '../components/StoryStrip'
import ChatList from '../components/ChatList'
import ChatWindow from '../components/ChatWindow'
import ProfilePanel from '../components/ProfilePanel'
import { useAuthContext } from '../hooks/useAuthContext'
import { getRoomMessages, getUserRooms } from '../services/chatService'
import { useWebSocket } from '../hooks/useWebSocket'
import { formatMessage, formatMessages } from '../utils/messageUtils'

const ChatPage = () => {
  const { user } = useAuthContext()
  const [rooms, setRooms] = useState([])
  const [roomsLoading, setRoomsLoading] = useState(true)
  const [selectedRoomId, setSelectedRoomId] = useState(null)
  const [messagesByRoom, setMessagesByRoom] = useState({})
  const [messagesLoading, setMessagesLoading] = useState(false)
  const [sendPending, setSendPending] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')
  const { subscribeToRoom, unsubscribeFromRoom, sendRoomMessage, connected } =
    useWebSocket(Boolean(user))

  const filteredRooms = useMemo(() => {
    if (!searchTerm.trim()) return rooms
    return rooms.filter((room) =>
      (room.name ?? room.roomName ?? '')
        .toLowerCase()
        .includes(searchTerm.trim().toLowerCase()),
    )
  }, [rooms, searchTerm])

  const selectedRoom = useMemo(
    () =>
      rooms.find(
        (room) => (room.id ?? room.roomId)?.toString() === selectedRoomId,
      ),
    [rooms, selectedRoomId],
  )

  const highlights = useMemo(
    () =>
      rooms.slice(0, 8).map((room) => ({
        id: room.id ?? room.roomId,
        name: room.name ?? room.roomName,
        avatar: room.avatar,
        isNew: (room.unread ?? room.unreadCount ?? 0) > 0,
      })),
    [rooms],
  )

  const loadRooms = useCallback(async () => {
    if (!user?.id && !user?.userId) return
    setRoomsLoading(true)
    try {
      const data = await getUserRooms(user.userId ?? user.id)
      setRooms(data)
      if (!selectedRoomId && data.length) {
        setSelectedRoomId((data[0].id ?? data[0].roomId).toString())
      }
    } catch (err) {
      toast.error(err?.response?.data?.error ?? 'Failed to load chats')
    } finally {
      setRoomsLoading(false)
    }
  }, [user, selectedRoomId])

  const loadMessages = useCallback(
    async (roomId) => {
      if (!roomId) return
      setMessagesLoading(true)
      try {
        const data = await getRoomMessages(roomId)
        setMessagesByRoom((prev) => ({
          ...prev,
          [roomId]: formatMessages(data, user?.username ?? user?.userName),
        }))
      } catch (err) {
        toast.error(err?.response?.data?.error ?? 'Failed to load messages')
      } finally {
        setMessagesLoading(false)
      }
    },
    [user],
  )

  useEffect(() => {
    loadRooms()
  }, [loadRooms])

  useEffect(() => {
    if (!selectedRoomId) return
    loadMessages(selectedRoomId)
  }, [loadMessages, selectedRoomId])

  useEffect(() => {
    if (!selectedRoomId || !connected) return
    const handler = (message) => {
      setMessagesByRoom((prev) => {
        const formatted = formatMessage(
          message,
          user?.username ?? user?.userName,
        )
        return {
          ...prev,
          [selectedRoomId]: [...(prev[selectedRoomId] ?? []), formatted],
        }
      })
    }

    subscribeToRoom(selectedRoomId, handler)
    return () => unsubscribeFromRoom(selectedRoomId)
  }, [
    connected,
    selectedRoomId,
    subscribeToRoom,
    unsubscribeFromRoom,
    user?.username,
    user?.userName,
  ])

  const handleSendMessage = async (content) => {
    if (!selectedRoomId) return
    const tempId =
      typeof crypto !== 'undefined' && crypto.randomUUID
        ? crypto.randomUUID()
        : `${Date.now()}`
    const optimisticMessage = {
      id: tempId,
      content,
      timestamp: new Date().toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
      }),
      senderId: user?.username ?? user?.userName,
      isOwn: true,
      status: 'sending',
    }

    setMessagesByRoom((prev) => ({
      ...prev,
      [selectedRoomId]: [...(prev[selectedRoomId] ?? []), optimisticMessage],
    }))

    try {
      setSendPending(true)
      sendRoomMessage({
        senderId: user?.username ?? user?.userName,
        content,
        type: 'CHAT',
        chatRoomId: selectedRoomId,
      })
    } catch (err) {
      toast.error(err?.response?.data?.error ?? 'Unable to send message')
    } finally {
      setSendPending(false)
    }
  }

  const currentMessages = messagesByRoom[selectedRoomId] ?? []

  return (
    <div className="flex h-screen bg-[#eef2f7]">
      <section className="w-full max-w-md border-r border-slate-200 bg-white/80 backdrop-blur">
        <div className="flex items-center justify-between border-b border-slate-100 px-6 py-4">
          <div>
            <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
              Telegram-inspired
            </p>
            <h2 className="text-xl font-semibold text-slate-900">Chats</h2>
          </div>
          <div className="flex items-center gap-2">
            {!connected && (
              <span className="flex items-center gap-1 text-xs text-amber-500">
                <Loader2 size={14} className="animate-spin" />
                Reconnecting
              </span>
            )}
            <button
              className="rounded-full border border-slate-200 p-2 text-slate-500 hover:text-slate-900"
              onClick={loadRooms}
            >
              <Filter size={18} />
            </button>
          </div>
        </div>

        <div className="space-y-6 px-6 py-5">
          <div className="flex items-center gap-2 rounded-2xl bg-slate-100 px-4 py-2 text-sm text-slate-500">
            <Search size={16} />
            <input
              className="flex-1 bg-transparent text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none"
              placeholder="Search"
              value={searchTerm}
              onChange={(event) => setSearchTerm(event.target.value)}
            />
          </div>

          {highlights.length > 0 && <StoryStrip stories={highlights} />}

          <div className="space-y-3">
            <div className="flex items-center justify-between text-xs uppercase tracking-[0.3em] text-slate-400">
              <span>Recent</span>
              <span>{rooms.length} chats</span>
            </div>
            {roomsLoading ? (
              <div className="flex flex-col items-center gap-2 rounded-2xl border border-dashed border-slate-200 px-4 py-8 text-sm text-slate-500">
                <Loader2 className="animate-spin" size={18} />
                Syncing rooms…
              </div>
            ) : (
              <ChatList
                chats={filteredRooms}
                selectedChatId={selectedRoomId}
                onSelect={(id) => setSelectedRoomId(id.toString())}
              />
            )}
          </div>
        </div>
      </section>

      <section className="hidden flex-1 lg:flex">
        <ChatWindow
          room={selectedRoom}
          messages={currentMessages}
          onSend={handleSendMessage}
          isSending={sendPending}
          messagesLoading={messagesLoading}
        />
        <ProfilePanel chat={selectedRoom} />
      </section>

      <section className="flex flex-1 lg:hidden">
        <ChatWindow
          room={selectedRoom}
          messages={currentMessages}
          onSend={handleSendMessage}
          isSending={sendPending}
          messagesLoading={messagesLoading}
        />
      </section>
    </div>
  )
}

export default ChatPage
