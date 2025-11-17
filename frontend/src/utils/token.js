export const tokenUtils = {
  getToken: () => localStorage.getItem('authToken'),

  setToken: (token) => localStorage.setItem('authToken', token),

  removeToken: () => localStorage.removeItem('authToken'),

  isTokenValid: () => {
    const token = localStorage.getItem('authToken')
    if (!token) return false

    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return payload.exp * 1000 > Date.now()
    } catch (error) {
      console.error('Invalid token payload', error)
      return false
    }
  },

  getUserFromToken: () => {
    const token = localStorage.getItem('authToken')
    if (!token) return null
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      return {
        username: payload.sub ?? payload.username,
        userId: payload.userId ?? payload.id,
      }
    } catch {
      return null
    }
  },
}

