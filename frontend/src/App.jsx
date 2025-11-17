import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import AppLayout from './layouts/AppLayout'
import ChatPage from './pages/ChatPage'
import ContactsPage from './pages/ContactsPage'
import CallsPage from './pages/CallsPage'
import SettingsPage from './pages/SettingsPage'
import AuthPage from './pages/AuthPage'
import ProtectedRoute from './components/ProtectedRoute'

const router = createBrowserRouter([
  {
    element: <ProtectedRoute />,
    children: [
      {
        path: '/',
        element: <AppLayout />,
        children: [
          { index: true, element: <ChatPage /> },
          { path: 'contacts', element: <ContactsPage /> },
          { path: 'calls', element: <CallsPage /> },
          { path: 'settings', element: <SettingsPage /> },
        ],
      },
    ],
  },
  {
    path: '/auth',
    element: <AuthPage />,
  },
])

const App = () => <RouterProvider router={router} />

export default App