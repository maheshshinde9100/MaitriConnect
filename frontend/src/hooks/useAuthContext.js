import { useContext } from 'react'
import { AuthContext } from '../context/AuthContextBase'

export const useAuthContext = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuthContext must be used within AuthProvider')
  }
  return ctx
}

