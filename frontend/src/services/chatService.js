import api from '../config/api';

export const getRoomMessages = async (roomId) => {
  try {
    const response = await api.get(`/api/chat/rooms/${roomId}/messages`);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.error || 'Failed to fetch messages');
  }
};

export const createChatRoom = async (roomData) => {
  try {
    const response = await api.post('/api/chat/rooms', roomData);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.error || 'Failed to create room');
  }
};

export const getUserRooms = async (userId) => {
  try {
    const response = await api.get(`/api/chat/users/${userId}/rooms`);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.error || 'Failed to fetch rooms');
  }
};