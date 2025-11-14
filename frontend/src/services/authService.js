import api from '../config/api';

export const registerUser = async (userData) => {
  try {
    const response = await api.post('/api/auth/register', userData);
    
    // Store token in localStorage
    localStorage.setItem('authToken', response.data.token);
    localStorage.setItem('userId', response.data.userId);
    localStorage.setItem('username', response.data.username);
    
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.error || 'Registration failed');
  }
};

export const loginUser = async (credentials) => {
  try {
    const response = await api.post('/api/auth/login', credentials);
    
    // Store authentication data
    localStorage.setItem('authToken', response.data.token);
    localStorage.setItem('userId', response.data.userId);
    localStorage.setItem('username', response.data.username);
    
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.error || 'Login failed');
  }
};

export const getUserDetails = async (userId) => {
  try {
    const response = await api.get(`/api/auth/user/${userId}`);
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.error || 'Failed to fetch user details');
  }
};

export const logoutUser = () => {
  localStorage.removeItem('authToken');
  localStorage.removeItem('userId');
  localStorage.removeItem('username');
};