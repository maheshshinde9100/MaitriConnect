import api from '../config/api'

const AUTH_BASE = '/api/auth'

export const registerUser = async (payload) => {
  const response = await api.post(`${AUTH_BASE}/register`, payload)
  return response.data
}

export const loginUser = async (payload) => {
  const response = await api.post(`${AUTH_BASE}/login`, payload)
  return response.data
}

export const getUserDetails = async (userId) => {
  const response = await api.get(`${AUTH_BASE}/user/${userId}`)
  return response.data
}

