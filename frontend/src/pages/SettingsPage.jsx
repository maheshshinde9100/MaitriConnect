import { useState } from 'react'
import { Bell, Lock, Moon, Palette, Shield, Smartphone } from 'lucide-react'

const settingsSections = [
  {
    id: 'privacy',
    title: 'Privacy & Security',
    description: 'Control who sees your info and activity',
    icon: Shield,
  },
  {
    id: 'notifications',
    title: 'Notifications',
    description: 'Customize alerts, sounds and schedules',
    icon: Bell,
  },
  {
    id: 'devices',
    title: 'Devices',
    description: 'Review and manage active sessions',
    icon: Smartphone,
  },
]

const SettingsPage = () => {
  const [isDarkMode, setIsDarkMode] = useState(false)
  const [readReceipts, setReadReceipts] = useState(true)

  return (
    <div className="flex h-screen flex-col bg-[#f5f7fb] px-6 py-8">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
            Preferences
          </p>
          <h2 className="text-2xl font-semibold text-slate-900">Settings</h2>
        </div>
        <button className="rounded-full border border-slate-200 px-4 py-2 text-sm text-slate-500 hover:bg-white">
          Manage subscription
        </button>
      </div>

      <div className="mt-8 grid gap-6 lg:grid-cols-[2fr_1fr]">
        <div className="space-y-5">
          {settingsSections.map((section) => (
            <div
              key={section.id}
              className="flex items-center gap-4 rounded-3xl border border-slate-200 bg-white p-5"
            >
              <div className="rounded-2xl bg-slate-100 p-3 text-slate-600">
                <section.icon size={22} />
              </div>
              <div>
                <p className="font-semibold text-slate-900">{section.title}</p>
                <p className="text-sm text-slate-500">{section.description}</p>
              </div>
            </div>
          ))}
        </div>

        <div className="space-y-5">
          <div className="rounded-3xl border border-slate-200 bg-white p-5">
            <div className="flex items-center justify-between">
              <div>
                <p className="font-semibold text-slate-900">Dark mode</p>
                <p className="text-sm text-slate-500">Auto sync with Telegram feel</p>
              </div>
              <button
                onClick={() => setIsDarkMode((prev) => !prev)}
                className={[
                  'flex items-center gap-2 rounded-full border px-3 py-1.5 text-sm',
                  isDarkMode
                    ? 'border-slate-900 bg-slate-900 text-white'
                    : 'border-slate-200 text-slate-500',
                ].join(' ')}
              >
                <Moon size={16} />
                {isDarkMode ? 'On' : 'Off'}
              </button>
            </div>
          </div>

          <div className="rounded-3xl border border-slate-200 bg-white p-5">
            <div className="flex items-center justify-between">
              <div>
                <p className="font-semibold text-slate-900">Read receipts</p>
                <p className="text-sm text-slate-500">
                  Control seen indicators
                </p>
              </div>
              <button
                onClick={() => setReadReceipts((prev) => !prev)}
                className={[
                  'flex items-center gap-2 rounded-full border px-3 py-1.5 text-sm',
                  readReceipts
                    ? 'border-emerald-200 bg-emerald-50 text-emerald-600'
                    : 'border-slate-200 text-slate-500',
                ].join(' ')}
              >
                <Lock size={16} />
                {readReceipts ? 'Enabled' : 'Hidden'}
              </button>
            </div>
          </div>

          <div className="rounded-3xl border border-slate-200 bg-white p-5">
            <p className="font-semibold text-slate-900">Accent color</p>
            <div className="mt-3 flex gap-3">
              {['#40a7e3', '#34bfa3', '#d09eff', '#f97316'].map((color) => (
                <button
                  key={color}
                  className="h-10 w-10 rounded-2xl border-2 border-white shadow ring-1 ring-black/5"
                  style={{ backgroundColor: color }}
                  title="Pick accent"
                />
              ))}
            </div>
            <div className="mt-4 flex items-center gap-2 text-sm text-slate-500">
              <Palette size={16} />
              Real theme logic will hook into Tailwind tokens later.
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default SettingsPage

