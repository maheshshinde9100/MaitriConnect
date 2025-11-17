import { useEffect, useState } from 'react'
import { Search, UserPlus } from 'lucide-react'
import { getUserRooms } from '../services/chatService'
import { useAuthContext } from '../hooks/useAuthContext'

const ContactsPage = () => {
  const { user } = useAuthContext()
  const [contacts, setContacts] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const loadContacts = async () => {
      setLoading(true)
      try {
        const rooms = await getUserRooms(user.userId ?? user.id)
        const mapped = rooms
          .filter(
            (room) => room.type === 'DIRECT' || room.participants?.length === 2,
          )
          .map((room) => {
            const otherParticipant = room.participants?.find(
              (participant) =>
                participant.username !== (user.username ?? user.userName),
            )
            return {
              id: room.id ?? room.roomId,
              name: otherParticipant?.displayName ?? otherParticipant?.username ?? room.name,
              about: otherParticipant?.about ?? 'Reachable on MaitriConnect',
              avatar: otherParticipant?.avatar,
              online: otherParticipant?.online,
            }
          })
        setContacts(mapped)
      } catch {
        setContacts([])
      } finally {
        setLoading(false)
      }
    }

    if (user) {
      loadContacts()
    }
  }, [user])

  return (
    <div className="flex h-screen bg-[#f5f7fb]">
      <div className="w-full max-w-lg border-r border-slate-200 bg-white/90 backdrop-blur">
        <div className="flex items-center justify-between border-b border-slate-100 px-6 py-4">
          <div>
            <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
              Directory
            </p>
            <h2 className="text-xl font-semibold text-slate-900">Contacts</h2>
          </div>
          <button className="flex items-center gap-2 rounded-full bg-slate-900 px-3 py-1.5 text-sm text-white">
            <UserPlus size={16} />
            New
          </button>
        </div>

        <div className="space-y-6 px-6 py-5">
          <div className="flex items-center gap-2 rounded-2xl bg-slate-100 px-4 py-2 text-sm text-slate-500">
            <Search size={16} />
            <input
              className="flex-1 bg-transparent text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none"
              placeholder="Search contacts"
            />
          </div>

          <div className="space-y-2">
            {loading ? (
              <p className="rounded-2xl border border-dashed border-slate-200 p-6 text-center text-sm text-slate-500">
                Syncing contacts…
              </p>
            ) : contacts.length === 0 ? (
              <p className="rounded-2xl border border-dashed border-slate-200 p-6 text-center text-sm text-slate-500">
                No contacts found yet. Create a direct chat to populate this list.
              </p>
            ) : (
              contacts.map((contact) => (
                <div
                  key={contact.id}
                  className="flex items-center gap-4 rounded-2xl px-3 py-3 hover:bg-slate-50"
                >
                  <div className="relative">
                    {contact.avatar ? (
                      <img
                        src={contact.avatar}
                        alt={contact.name}
                        className="h-12 w-12 rounded-full object-cover"
                      />
                    ) : (
                      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-slate-200 text-sm font-semibold text-slate-600">
                        {contact.name?.slice(0, 2)?.toUpperCase()}
                      </div>
                    )}
                    {contact.online && (
                      <span className="absolute bottom-0 right-0 h-3 w-3 rounded-full border-2 border-white bg-emerald-400" />
                    )}
                  </div>
                  <div>
                    <p className="font-semibold text-slate-900">{contact.name}</p>
                    <p className="text-sm text-slate-500">{contact.about}</p>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      <div className="hidden flex-1 items-center justify-center text-slate-400 lg:flex">
        Select a contact to preview profile
      </div>
    </div>
  )
}

export default ContactsPage

