import api from '../config/api'

const CHAT_BASE = '/api/chat'

export const getUserRooms = async (userId) => {
  const response = await api.get(`${CHAT_BASE}/users/${userId}/rooms`)
  return response.data
}

export const getRoomMessages = async (roomId) => {
  const response = await api.get(`${CHAT_BASE}/rooms/${roomId}/messages`)
  return response.data
}

export const createChatRoom = async (payload) => {
  const response = await api.post(`${CHAT_BASE}/rooms`, payload)
  return response.data
}

