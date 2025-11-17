import { useCallback, useEffect, useMemo, useState } from 'react'
import { getUserDetails, loginUser, registerUser } from '../services/authService'
import { tokenUtils } from '../utils/token'
import { AuthContext } from './AuthContextBase'

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [initializing, setInitializing] = useState(true)
  const [authLoading, setAuthLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    const bootstrap = async () => {
      const hasValidToken = tokenUtils.isTokenValid()
      if (!hasValidToken) {
        tokenUtils.removeToken()
        localStorage.removeItem('userId')
        localStorage.removeItem('username')
        setInitializing(false)
        return
      }

      try {
        const storedUser =
          tokenUtils.getUserFromToken() ?? {
            userId: localStorage.getItem('userId'),
            username: localStorage.getItem('username'),
          }

        if (storedUser?.userId) {
          const profile = await getUserDetails(storedUser.userId)
          setUser(profile)
        }
      } catch (err) {
        console.error('Failed to bootstrap auth', err)
        tokenUtils.removeToken()
      } finally {
        setInitializing(false)
      }
    }

    bootstrap()
  }, [])

  const persistSession = (payload) => {
    tokenUtils.setToken(payload.token)
    localStorage.setItem('userId', payload.userId)
    localStorage.setItem('username', payload.username)
  }

  const handleAuth = useCallback(
    (action) =>
      async (credentials) => {
        setAuthLoading(true)
        setError(null)
        try {
          const response =
            action === 'login'
              ? await loginUser(credentials)
              : await registerUser(credentials)

          persistSession(response)
          const profile = await getUserDetails(response.userId)
          setUser(profile)
          return profile
        } catch (err) {
          const message =
            err?.response?.data?.error ?? err.message ?? 'Authentication failed'
          setError(message)
          throw err
        } finally {
          setAuthLoading(false)
        }
      },
    [],
  )

  const logout = () => {
    tokenUtils.removeToken()
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    setUser(null)
  }

  const value = useMemo(
    () => ({
      user,
      initializing,
      authLoading,
      error,
      login: handleAuth('login'),
      register: handleAuth('register'),
      logout,
      token: tokenUtils.getToken(),
      isAuthenticated: Boolean(user),
    }),
    [user, initializing, authLoading, error, handleAuth],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

