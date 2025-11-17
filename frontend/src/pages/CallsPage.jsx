import { Phone, Video } from 'lucide-react'

const callHistory = [
  {
    id: 'call-1',
    name: 'Aarav Jain',
    type: 'voice',
    direction: 'outgoing',
    time: 'Today · 10:24',
    avatar: 'https://i.pravatar.cc/150?img=66',
    status: 'missed',
  },
  {
    id: 'call-2',
    name: 'Mia Robertson',
    type: 'video',
    direction: 'incoming',
    time: 'Yesterday · 19:10',
    avatar: 'https://i.pravatar.cc/150?img=54',
    status: 'connected',
  },
  {
    id: 'call-3',
    name: 'Product Squad',
    type: 'voice',
    direction: 'outgoing',
    time: 'Mon · 08:32',
    avatar: 'https://i.pravatar.cc/150?img=15',
    status: 'connected',
  },
]

const CallsPage = () => {
  return (
    <div className="flex h-screen flex-col bg-[#f5f7fb] px-6 py-8">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
            Activity
          </p>
          <h2 className="text-2xl font-semibold text-slate-900">Calls</h2>
        </div>
        <div className="flex gap-3">
          <button className="flex items-center gap-2 rounded-full bg-slate-900 px-4 py-2 text-sm text-white">
            <Phone size={16} />
            Voice
          </button>
          <button className="flex items-center gap-2 rounded-full border border-slate-200 px-4 py-2 text-sm text-slate-600 hover:bg-white">
            <Video size={16} />
            Video
          </button>
        </div>
      </div>

      <div className="mt-8 space-y-3">
        {callHistory.map((call) => (
          <div
            key={call.id}
            className="flex items-center gap-4 rounded-3xl border border-slate-200 bg-white px-4 py-3"
          >
            <img
              src={call.avatar}
              alt={call.name}
              className="h-12 w-12 rounded-full object-cover"
            />
            <div className="flex-1">
              <div className="flex items-center gap-2">
                <p className="font-semibold text-slate-900">{call.name}</p>
                <span
                  className={[
                    'text-xs font-medium',
                    call.status === 'missed' ? 'text-rose-500' : 'text-emerald-500',
                  ].join(' ')}
                >
                  {call.status}
                </span>
              </div>
              <p className="text-sm text-slate-500">
                {call.direction === 'incoming' ? 'Incoming' : 'Outgoing'} ·{' '}
                {call.time}
              </p>
            </div>
            <div className="rounded-full border border-slate-200 p-2 text-slate-500">
              {call.type === 'voice' ? <Phone size={18} /> : <Video size={18} />}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default CallsPage

