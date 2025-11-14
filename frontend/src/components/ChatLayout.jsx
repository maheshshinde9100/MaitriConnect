import React, { useState } from 'react';
import { motion } from 'framer-motion';
import RoomList from './RoomList';
import Chat from './Chat';
import MobileMenu from './MobileMenu';

const ChatLayout = () => {
  const [selectedRoom, setSelectedRoom] = useState('public');

  return (
    <div className="flex h-full">
      {/* Desktop Sidebar with room list */}
      <motion.div
        initial={{ x: -250 }}
        animate={{ x: 0 }}
        transition={{ duration: 0.3 }}
        className="hidden md:block"
      >
        <RoomList 
          onRoomSelect={setSelectedRoom}
          selectedRoom={selectedRoom}
        />
      </motion.div>

      {/* Main chat area */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.2 }}
        className="flex-1 flex flex-col"
      >
        {/* Mobile Menu Button */}
        <div className="md:hidden p-4 border-b border-border-color bg-secondary">
          <div className="flex items-center justify-between">
            <MobileMenu
              onRoomSelect={setSelectedRoom}
              selectedRoom={selectedRoom}
            />
            <h2 className="font-semibold">
              {selectedRoom === 'public' ? 'Public Chat' : `Room ${selectedRoom}`}
            </h2>
            <div></div> {/* Spacer for centering */}
          </div>
        </div>

        <Chat roomId={selectedRoom} />
      </motion.div>
    </div>
  );
};

export default ChatLayout;