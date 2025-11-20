import { useEffect, useState } from 'react'
import { ArrowRight, Lock, Sparkles, UserPlus } from 'lucide-react'
import { Link, useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { useAuthContext } from '../hooks/useAuthContext'

const AuthPage = () => {
  const navigate = useNavigate()
  const { login, register, authLoading, error, isAuthenticated, initializing } =
    useAuthContext()
  const [mode, setMode] = useState('login')
  const [credentials, setCredentials] = useState({
    username: '',
    password: '',
    email: '',
    firstName: '',
    lastName: '',
  })

  const handleChange = (field) => (event) => {
    setCredentials((prev) => ({
      ...prev,
      [field]: event.target.value,
    }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()

    const payload =
      mode === 'login'
        ? { username: credentials.username, password: credentials.password }
        : {
            username: credentials.username,
            password: credentials.password,
            email: credentials.email,
            firstName: credentials.firstName,
            lastName: credentials.lastName,
          }

    try {
      if (mode === 'login') {
        await login(payload)
      } else {
        await register(payload)
      }
      toast.success(mode === 'login' ? 'Welcome back!' : 'Account created')
      navigate('/')
    } catch (err) {
      toast.error(err?.response?.data?.error ?? 'Authentication failed')
    }
  }

  const switchMode = () => {
    setMode((prev) => (prev === 'login' ? 'register' : 'login'))
  }

  useEffect(() => {
    if (!initializing && isAuthenticated) {
      navigate('/')
    }
  }, [isAuthenticated, initializing, navigate])

  return (
    <div className="relative min-h-screen bg-gradient-to-br from-slate-900 via-slate-900 to-indigo-900 text-white">
      <div className="absolute inset-0 opacity-40">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,_rgba(255,255,255,0.18),_transparent_55%)]" />
      </div>

      <div className="relative z-10 flex min-h-screen flex-col items-center justify-center px-4 py-16">
        <div className="mb-10 flex items-center gap-3 rounded-full border border-white/20 px-4 py-2 text-sm text-white/80 backdrop-blur">
          <Sparkles size={16} />
          Secure messaging for teams & communities
        </div>

        <div className="grid w-full max-w-6xl grid-cols-1 gap-8 rounded-3xl bg-white/5 p-10 shadow-2xl backdrop-blur lg:grid-cols-2">
          <div className="space-y-8">
            <div>
              <p className="text-sm uppercase tracking-[0.3em] text-white/60">
                MaitriConnect
              </p>
              <h1 className="mt-3 text-4xl font-semibold leading-tight">
                Private, fast, and minimal chat experience inspired by Telegram
              </h1>
            </div>
            <div className="space-y-4 text-white/70">
              <p className="flex items-center gap-2">
                <span className="h-1.5 w-1.5 rounded-full bg-emerald-400" />
                End-to-end encryption ready
              </p>
              <p className="flex items-center gap-2">
                <span className="h-1.5 w-1.5 rounded-full bg-sky-400" />
                Multi-device sync and voice/video calls
              </p>
              <p className="flex items-center gap-2">
                <span className="h-1.5 w-1.5 rounded-full bg-indigo-400" />
                Message scheduling & reminders
              </p>
            </div>
          </div>

          <div className="rounded-2xl bg-white/10 p-8 shadow-xl">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-semibold uppercase tracking-widest text-white/60">
                  {mode === 'login' ? 'Welcome back' : 'Create account'}
                </p>
                <p className="text-white/70">
                  {mode === 'login'
                    ? 'Sign in using your username and password'
                    : 'Provision a workspace-ready Telegram-style account'}
                </p>
              </div>
              <button
                onClick={switchMode}
                className="flex items-center gap-2 rounded-full border border-white/20 px-3 py-1.5 text-sm text-white/80"
              >
                <UserPlus size={16} />
                {mode === 'login' ? 'New user' : 'Have an account'}
              </button>
            </div>

            <form className="mt-6 space-y-4" onSubmit={handleSubmit}>
              <label className="block text-sm text-white/80">
                Username
                <input
                  className="mt-2 w-full rounded-2xl border border-white/10 bg-white/10 px-4 py-3 text-white placeholder-white/40 focus:border-white/40 focus:outline-none"
                  value={credentials.username}
                  onChange={handleChange('username')}
                  placeholder="jane.doe"
                  required
                />
              </label>

              {mode === 'register' && (
                <>
                  <label className="block text-sm text-white/80">
                    Email
                    <input
                      type="email"
                      className="mt-2 w-full rounded-2xl border border-white/10 bg-white/10 px-4 py-3 text-white placeholder-white/40 focus:border-white/40 focus:outline-none"
                      value={credentials.email}
                      onChange={handleChange('email')}
                      placeholder="jane@maitri.com"
                      required
                    />
                  </label>
                  <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <label className="block text-sm text-white/80">
                      First name
                      <input
                        className="mt-2 w-full rounded-2xl border border-white/10 bg-white/10 px-4 py-3 text-white placeholder-white/40 focus:border-white/40 focus:outline-none"
                        value={credentials.firstName}
                        onChange={handleChange('firstName')}
                        placeholder="Jane"
                        required
                      />
                    </label>
                    <label className="block text-sm text-white/80">
                      Last name
                      <input
                        className="mt-2 w-full rounded-2xl border border-white/10 bg-white/10 px-4 py-3 text-white placeholder-white/40 focus:border-white/40 focus:outline-none"
                        value={credentials.lastName}
                        onChange={handleChange('lastName')}
                        placeholder="Doe"
                        required
                      />
                    </label>
                  </div>
                </>
              )}

              <label className="block text-sm text-white/80">
                Password
                <input
                  type="password"
                  className="mt-2 w-full rounded-2xl border border-white/10 bg-white/10 px-4 py-3 text-white placeholder-white/40 focus:border-white/40 focus:outline-none"
                  value={credentials.password}
                  onChange={handleChange('password')}
                  placeholder="••••••••"
                  required
                />
              </label>

              <button
                type="submit"
                disabled={authLoading}
                className="flex w-full items-center justify-center gap-2 rounded-2xl bg-white px-4 py-3 text-lg font-medium text-slate-900 transition hover:bg-slate-100 disabled:cursor-not-allowed disabled:opacity-70"
              >
                {authLoading ? 'Processing...' : 'Continue'}
                <ArrowRight size={18} />
              </button>

              {(error || mode === 'register') && (
                <p className="flex items-center gap-2 text-sm text-white/70">
                  <Lock size={14} />
                  {error
                    ? error
                    : 'Secured with encrypted verification'}
                </p>
              )}
            </form>

            <div className="mt-6 text-center text-sm text-white/60">
              Need help?
              <Link to="/" className="ml-1 text-white hover:underline">
                Contact admin
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default AuthPage
