import { NavLink } from 'react-router-dom'
import {
  MessageSquare,
  Users,
  Phone,
  Settings,
  Plus,
  LogIn,
} from 'lucide-react'

const navItems = [
  { id: 'chats', icon: MessageSquare, to: '/', label: 'Chats' },
  { id: 'contacts', icon: Users, to: '/contacts', label: 'Contacts' },
  { id: 'calls', icon: Phone, to: '/calls', label: 'Calls' },
  { id: 'settings', icon: Settings, to: '/settings', label: 'Settings' },
]

const NavigationRail = () => {
  return (
    <aside className="hidden w-20 flex-col border-r border-slate-200 bg-white/80 backdrop-blur md:flex">
      <div className="flex flex-1 flex-col items-center gap-6 py-6">
        <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-gradient-to-br from-sky-500 to-indigo-500 text-white font-semibold">
          MC
        </div>

        <button className="flex h-11 w-11 items-center justify-center rounded-2xl bg-slate-900 text-white shadow-lg shadow-slate-900/10 hover:bg-slate-800">
          <Plus size={20} />
        </button>

        <nav className="flex flex-1 flex-col items-center gap-4 pt-4">
          {navItems.map((item) => (
            <NavLink
              key={item.id}
              to={item.to}
              className={({ isActive }) =>
                [
                  'flex h-11 w-11 items-center justify-center rounded-2xl text-slate-500 transition',
                  isActive ? 'bg-slate-900 text-white shadow-lg shadow-slate-900/10' : 'hover:bg-slate-100',
                ].join(' ')
              }
              title={item.label}
            >
              <item.icon size={20} />
            </NavLink>
          ))}
        </nav>

        <NavLink
          to="/auth"
          className="flex h-10 w-10 items-center justify-center rounded-2xl border border-slate-200 text-slate-400 hover:text-slate-900"
          title="Switch account"
        >
          <LogIn size={18} />
        </NavLink>
      </div>
    </aside>
  )
}

export default NavigationRail

