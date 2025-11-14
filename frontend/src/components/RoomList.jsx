import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Plus, Hash, Users, Settings } from 'lucide-react';
import { getUserRooms, createChatRoom } from '../services/chatService';
import { useAuth } from '../contexts/AuthContext';
import toast from 'react-hot-toast';

const RoomList = ({ onRoomSelect, selectedRoom }) => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateRoom, setShowCreateRoom] = useState(false);
  const [newRoomName, setNewRoomName] = useState('');
  const { user } = useAuth();

  useEffect(() => {
    if (user) {
      loadRooms();
    }
  }, [user]);

  const loadRooms = async () => {
    try {
      setLoading(true);
      const userRooms = await getUserRooms(user.userId);
      setRooms(userRooms);
    } catch (error) {
      toast.error('Failed to load rooms');
      console.error('Error loading rooms:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateRoom = async (e) => {
    e.preventDefault();
    if (!newRoomName.trim()) return;

    try {
      const roomData = {
        name: newRoomName,
        participants: [user.userId],
        createdBy: user.userId
      };
      
      const newRoom = await createChatRoom(roomData);
      setRooms(prev => [...prev, newRoom]);
      setNewRoomName('');
      setShowCreateRoom(false);
      toast.success('Room created successfully!');
    } catch (error) {
      toast.error('Failed to create room');
      console.error('Error creating room:', error);
    }
  };

  return (
    <div className="w-64 bg-secondary border-r border-border-color flex flex-col">
      {/* Header */}
      <div className="p-4 border-b border-border-color">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold text-primary">Rooms</h3>
          <motion.button
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => setShowCreateRoom(true)}
            className="btn btn-ghost btn-sm"
            title="Create new room"
          >
            <Plus size={16} />
          </motion.button>
        </div>

        {/* Create Room Form */}
        {showCreateRoom && (
          <motion.form
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            onSubmit={handleCreateRoom}
            className="mb-4"
          >
            <input
              type="text"
              value={newRoomName}
              onChange={(e) => setNewRoomName(e.target.value)}
              placeholder="Room name..."
              className="form-input mb-2"
              autoFocus
            />
            <div className="flex gap-2">
              <button type="submit" className="btn btn-primary btn-sm flex-1">
                Create
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowCreateRoom(false);
                  setNewRoomName('');
                }}
                className="btn btn-secondary btn-sm"
              >
                Cancel
              </button>
            </div>
          </motion.form>
        )}
      </div>

      {/* Room List */}
      <div className="flex-1 overflow-y-auto">
        {/* Public Room */}
        <motion.button
          whileHover={{ backgroundColor: 'var(--bg-tertiary)' }}
          onClick={() => onRoomSelect('public')}
          className={`w-full p-3 text-left flex items-center gap-3 transition-colors ${
            selectedRoom === 'public' ? 'bg-tertiary border-r-2 border-accent' : ''
          }`}
        >
          <Hash size={16} className="text-accent" />
          <span className="font-medium">Public Chat</span>
        </motion.button>

        {/* User Rooms */}
        {loading ? (
          <div className="p-4">
            <div className="animate-pulse space-y-2">
              {[1, 2, 3].map(i => (
                <div key={i} className="h-10 bg-tertiary rounded"></div>
              ))}
            </div>
          </div>
        ) : (
          rooms.map(room => (
            <motion.button
              key={room.id}
              whileHover={{ backgroundColor: 'var(--bg-tertiary)' }}
              onClick={() => onRoomSelect(room.id)}
              className={`w-full p-3 text-left flex items-center gap-3 transition-colors ${
                selectedRoom === room.id ? 'bg-tertiary border-r-2 border-accent' : ''
              }`}
            >
              <Users size={16} className="text-muted" />
              <div className="flex-1 min-w-0">
                <div className="font-medium truncate">{room.name}</div>
                <div className="text-xs text-muted">
                  {room.participants?.length || 0} members
                </div>
              </div>
            </motion.button>
          ))
        )}
      </div>

      {/* Settings */}
      <div className="p-4 border-t border-border-color">
        <motion.button
          whileHover={{ backgroundColor: 'var(--bg-tertiary)' }}
          className="w-full p-2 text-left flex items-center gap-3 rounded-md transition-colors text-muted hover:text-primary"
        >
          <Settings size={16} />
          <span className="text-sm">Settings</span>
        </motion.button>
      </div>
    </div>
  );
};

export default RoomList;