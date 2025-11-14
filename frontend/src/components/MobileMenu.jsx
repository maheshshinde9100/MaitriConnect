import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Menu, X } from 'lucide-react';
import RoomList from './RoomList';

const MobileMenu = ({ onRoomSelect, selectedRoom }) => {
  const [isOpen, setIsOpen] = useState(false);

  const toggleMenu = () => setIsOpen(!isOpen);

  return (
    <>
      {/* Menu Button */}
      <motion.button
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        onClick={toggleMenu}
        className="md:hidden btn btn-ghost btn-sm"
      >
        <Menu size={20} />
      </motion.button>

      {/* Mobile Menu Overlay */}
      <AnimatePresence>
        {isOpen && (
          <>
            {/* Backdrop */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={toggleMenu}
              className="fixed inset-0 bg-black/50 z-40 md:hidden"
            />

            {/* Menu Content */}
            <motion.div
              initial={{ x: -300 }}
              animate={{ x: 0 }}
              exit={{ x: -300 }}
              transition={{ type: 'spring', damping: 25, stiffness: 200 }}
              className="fixed left-0 top-0 h-full w-80 bg-secondary border-r border-border-color z-50 md:hidden"
            >
              {/* Header */}
              <div className="flex items-center justify-between p-4 border-b border-border-color">
                <h3 className="font-semibold text-primary">Menu</h3>
                <button
                  onClick={toggleMenu}
                  className="btn btn-ghost btn-sm"
                >
                  <X size={20} />
                </button>
              </div>

              {/* Room List */}
              <div className="h-full">
                <RoomList
                  onRoomSelect={(roomId) => {
                    onRoomSelect(roomId);
                    setIsOpen(false);
                  }}
                  selectedRoom={selectedRoom}
                />
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>
    </>
  );
};

export default MobileMenu;