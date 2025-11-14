// API Configuration
export const API_CONFIG = {
  BASE_URL: 'http://localhost:8080',
  WS_URL: 'http://localhost:8082/ws',
  AUTH_BASE: 'http://localhost:8080/api/auth',
  CHAT_BASE: 'http://localhost:8080/api/chat',
  TIMEOUT: 10000, // 10 seconds
};

// WebSocket Configuration
export const WS_CONFIG = {
  RECONNECT_DELAY: 5000,
  HEARTBEAT_INCOMING: 4000,
  HEARTBEAT_OUTGOING: 4000,
  MAX_RECONNECT_ATTEMPTS: 5,
};

// Message Types
export const MESSAGE_TYPES = {
  CHAT: 'CHAT',
  JOIN: 'JOIN',
  LEAVE: 'LEAVE',
  TYPING: 'TYPING',
  SYSTEM: 'SYSTEM',
};

// Theme Configuration
export const THEMES = {
  LIGHT: 'light',
  DARK: 'dark',
  SYSTEM: 'system',
};

// Local Storage Keys
export const STORAGE_KEYS = {
  AUTH_TOKEN: 'authToken',
  USER_ID: 'userId',
  USERNAME: 'username',
  THEME: 'theme',
  CHAT_SETTINGS: 'chatSettings',
  ROOM_PREFERENCES: 'roomPreferences',
};

// Validation Rules
export const VALIDATION = {
  USERNAME: {
    MIN_LENGTH: 3,
    MAX_LENGTH: 20,
    PATTERN: /^[a-zA-Z0-9_]{3,20}$/,
  },
  PASSWORD: {
    MIN_LENGTH: 6,
    MAX_LENGTH: 128,
  },
  EMAIL: {
    PATTERN: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  },
  ROOM_NAME: {
    MIN_LENGTH: 1,
    MAX_LENGTH: 50,
  },
  MESSAGE: {
    MAX_LENGTH: 1000,
  },
};

// UI Constants
export const UI = {
  ANIMATION_DURATION: 300,
  TOAST_DURATION: 4000,
  DEBOUNCE_DELAY: 300,
  THROTTLE_DELAY: 1000,
  MOBILE_BREAKPOINT: 768,
};

// Error Messages
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Network error - please check your connection',
  UNAUTHORIZED: 'Unauthorized - please login again',
  FORBIDDEN: 'Forbidden - insufficient permissions',
  NOT_FOUND: 'Resource not found',
  SERVER_ERROR: 'Server error - please try again later',
  VALIDATION_ERROR: 'Please check your input and try again',
  UNKNOWN_ERROR: 'An unexpected error occurred',
};

// Success Messages
export const SUCCESS_MESSAGES = {
  LOGIN_SUCCESS: 'Welcome back!',
  REGISTER_SUCCESS: 'Account created successfully!',
  LOGOUT_SUCCESS: 'Logged out successfully',
  ROOM_CREATED: 'Room created successfully!',
  MESSAGE_SENT: 'Message sent',
  CONNECTED: 'Connected to chat!',
  SETTINGS_SAVED: 'Settings saved successfully',
};

// Default Values
export const DEFAULTS = {
  ROOM_ID: 'public',
  THEME: THEMES.LIGHT,
  CHAT_SETTINGS: {
    notifications: true,
    soundEnabled: true,
    showTimestamps: true,
    compactMode: false,
  },
};

// Feature Flags
export const FEATURES = {
  ROOMS_ENABLED: true,
  NOTIFICATIONS_ENABLED: true,
  FILE_UPLOAD_ENABLED: false,
  VOICE_MESSAGES_ENABLED: false,
  VIDEO_CHAT_ENABLED: false,
  EMOJI_REACTIONS_ENABLED: true,
};

// Routes
export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  CHAT: '/chat',
  SETTINGS: '/settings',
  PROFILE: '/profile',
};

// WebSocket Events
export const WS_EVENTS = {
  CONNECT: 'connect',
  DISCONNECT: 'disconnect',
  MESSAGE: 'message',
  JOIN_ROOM: 'join_room',
  LEAVE_ROOM: 'leave_room',
  TYPING_START: 'typing_start',
  TYPING_STOP: 'typing_stop',
  USER_ONLINE: 'user_online',
  USER_OFFLINE: 'user_offline',
};

// HTTP Status Codes
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500,
};