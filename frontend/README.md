# 🚀 MaitriConnect Frontend

A modern, responsive React frontend for the MaitriConnect chat application with dark/light theme support and real-time messaging.

## ✨ Features

- **🔐 Authentication**: User registration and login with JWT tokens
- **💬 Real-time Chat**: WebSocket-based messaging with STOMP protocol
- **🌓 Theme Support**: Beautiful dark and light themes with smooth transitions
- **📱 Responsive Design**: Mobile-first design with responsive layouts
- **🏠 Room Management**: Create and join different chat rooms
- **🎨 Modern UI**: Clean, modern interface with smooth animations
- **⚡ Performance**: Optimized with React hooks and context API
- **🔔 Notifications**: Toast notifications for user feedback
- **♿ Accessibility**: WCAG compliant with proper focus management

## 🛠️ Tech Stack

- **React 19** - UI library
- **Vite** - Build tool and dev server
- **Framer Motion** - Animations and transitions
- **Axios** - HTTP client for API calls
- **STOMP.js** - WebSocket messaging protocol
- **SockJS** - WebSocket fallback support
- **React Hot Toast** - Notification system
- **Lucide React** - Beautiful icons
- **React Router DOM** - Client-side routing

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/          # React components
│   │   ├── Chat.jsx        # Main chat interface
│   │   ├── ChatLayout.jsx  # Chat layout with sidebar
│   │   ├── Header.jsx      # App header with theme toggle
│   │   ├── Login.jsx       # Authentication form
│   │   ├── RoomList.jsx    # Chat rooms sidebar
│   │   ├── MobileMenu.jsx  # Mobile navigation
│   │   ├── LoadingSpinner.jsx
│   │   └── ErrorBoundary.jsx
│   ├── contexts/           # React contexts
│   │   ├── AuthContext.jsx # Authentication state
│   │   └── ThemeContext.jsx # Theme management
│   ├── hooks/              # Custom React hooks
│   │   └── useWebSocket.js # WebSocket connection hook
│   ├── services/           # API services
│   │   ├── authService.js  # Authentication API
│   │   ├── chatService.js  # Chat API
│   │   └── websocketService.js # WebSocket service
│   ├── config/             # Configuration
│   │   └── api.js         # Axios configuration
│   ├── utils/              # Utility functions
│   │   └── helpers.js     # Common helper functions
│   ├── constants/          # App constants
│   │   └── index.js       # Configuration constants
│   ├── App.jsx            # Main app component
│   ├── main.jsx           # App entry point
│   ├── index.css          # Global styles with theme variables
│   └── App.css            # App-specific styles
├── public/                 # Static assets
├── package.json           # Dependencies and scripts
└── vite.config.js         # Vite configuration
```

## 🚀 Getting Started

### Prerequisites

- Node.js (v18 or higher)
- npm or yarn
- MaitriConnect backend server running

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure API endpoints**
   
   Update the API configuration in `src/config/api.js` if needed:
   ```javascript
   const API_CONFIG = {
     BASE_URL: 'http://localhost:8080',
     WS_URL: 'http://localhost:8082/ws',
     // ...
   };
   ```

4. **Start the development server**
   ```bash
   npm run dev
   ```

5. **Open your browser**
   
   Navigate to `http://localhost:5173`

## 🎨 Theme System

The app features a comprehensive theme system with CSS custom properties:

### Light Theme
- Clean, bright interface
- Blue accent colors
- Subtle shadows and borders

### Dark Theme
- Dark backgrounds with high contrast
- Blue accent colors optimized for dark mode
- Enhanced shadows for depth

### Theme Toggle
Users can switch between themes using the toggle button in the header. The theme preference is saved in localStorage.

## 📱 Responsive Design

The application is fully responsive with:

- **Mobile-first approach**: Optimized for mobile devices
- **Breakpoint system**: Responsive layouts for different screen sizes
- **Mobile menu**: Collapsible sidebar for mobile devices
- **Touch-friendly**: Large touch targets and smooth interactions

## 🔌 API Integration

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/user/{userId}` - Get user details

### Chat Endpoints
- `GET /api/chat/rooms/{roomId}/messages` - Get room messages
- `POST /api/chat/rooms` - Create chat room
- `GET /api/chat/users/{userId}/rooms` - Get user rooms

### WebSocket Connection
- **URL**: `ws://localhost:8082/ws`
- **Protocol**: STOMP over SockJS
- **Topics**: `/topic/public`, `/topic/room/{roomId}`
- **Destinations**: `/app/chat.sendMessage`, `/app/chat.addUser`

## 🔧 Configuration

### Environment Variables
Create a `.env` file in the root directory:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_WS_URL=http://localhost:8082/ws
VITE_APP_NAME=MaitriConnect
```

### Build Configuration
The app uses Vite for building and development. Configuration is in `vite.config.js`.

## 🧪 Development

### Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

### Code Style
- ESLint configuration for React
- Consistent naming conventions
- Component-based architecture
- Custom hooks for reusable logic

## 🚀 Deployment

### Build for Production
```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.

### Deploy to Static Hosting
The built app can be deployed to any static hosting service:
- Vercel
- Netlify
- GitHub Pages
- AWS S3 + CloudFront

## 🔒 Security Features

- **JWT Token Management**: Secure token storage and automatic refresh
- **Input Validation**: Client-side validation for all forms
- **XSS Protection**: Proper input sanitization
- **CSRF Protection**: Token-based authentication
- **Secure WebSocket**: Authentication required for WebSocket connections

## 🎯 Performance Optimizations

- **Code Splitting**: Lazy loading of components
- **Memoization**: React.memo and useMemo for expensive operations
- **Debouncing**: Input debouncing for search and typing indicators
- **Efficient Re-renders**: Optimized context usage and state management

## 🐛 Error Handling

- **Error Boundaries**: Catch and display React errors gracefully
- **API Error Handling**: Comprehensive error handling for API calls
- **WebSocket Reconnection**: Automatic reconnection on connection loss
- **User Feedback**: Clear error messages and loading states

## 🔄 State Management

- **React Context**: Global state management for auth and theme
- **Local State**: Component-level state with useState
- **Custom Hooks**: Reusable stateful logic
- **LocalStorage**: Persistent storage for user preferences

## 📚 Component Documentation

### Core Components

#### `<Login />`
Authentication form with registration and login modes.

#### `<Chat />`
Main chat interface with message display and input.

#### `<ChatLayout />`
Layout component with sidebar and main chat area.

#### `<Header />`
App header with theme toggle and user info.

#### `<RoomList />`
Sidebar component for room navigation and creation.

### Context Providers

#### `<AuthProvider />`
Manages authentication state and user data.

#### `<ThemeProvider />`
Handles theme switching and persistence.

### Custom Hooks

#### `useWebSocket()`
Manages WebSocket connection and messaging.

#### `useAuth()`
Provides authentication methods and state.

#### `useTheme()`
Provides theme state and toggle function.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the API documentation

---

**Made with ❤️ for seamless real-time communication**