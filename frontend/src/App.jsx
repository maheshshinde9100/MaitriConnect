import { useState } from 'react'
import './App.css'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import ChatRoom from './pages/ChatRoom'

function App() {
  const [isChatOpen,setChatOpen] = useState(false)
  return (
    <>
      <Navbar setChatOpen={setChatOpen}/>
      {isChatOpen ? (
        <ChatRoom/>
      ):(
        <Home/>
      )}
    </>
  )
}

export default App
