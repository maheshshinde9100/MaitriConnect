import axios from 'axios';

const API_CONFIG = {
  BASE_URL: 'http://localhost:8080',
  WS_URL: 'http://localhost:8082/ws',
  AUTH_BASE: 'http://localhost:8080/api/auth',
  CHAT_BASE: 'http://localhost:8080/api/chat'
};

const api = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      localStorage.removeItem('userId');
      localStorage.removeItem('username');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export { API_CONFIG };
export default api;