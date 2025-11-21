import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Smile } from 'lucide-react';

const QUICK_REACTIONS = ['❤️', '😂', '😮', '😢', '😡'];

export default function MessageReactions({ messageId, reactions = {}, onReact, currentUserId }) {
    const [showPicker, setShowPicker] = useState(false);

    const handleReaction = (emoji) => {
        if (onReact) {
            onReact(messageId, emoji);
        }
        setShowPicker(false);
    };

    const totalReactions = Object.values(reactions).reduce((sum, count) => sum + count, 0);

    return (
        <div className="relative inline-block">
            {/* Reaction Counts Display */}
            {totalReactions > 0 && (
                <div className="flex gap-1 flex-wrap mb-1">
                    {Object.entries(reactions).map(([emoji, count]) => (
                        count > 0 && (
                            <motion.button
                                key={emoji}
                                initial={{ scale: 0 }}
                                animate={{ scale: 1 }}
                                exit={{ scale: 0 }}
                                whileHover={{ scale: 1.1 }}
                                whileTap={{ scale: 0.95 }}
                                onClick={() => handleReaction(emoji)}
                                className="flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium hover-lift"
                                style={{
                                    background: 'var(--bg-tertiary)',
                                    border: '1px solid var(--border-color)',
                                }}
                            >
                                <span>{emoji}</span>
                                <span style={{ color: 'var(--text-secondary)' }}>{count}</span>
                            </motion.button>
                        )
                    ))}
                </div>
            )}

            {/* Add Reaction Button */}
            <button
                onClick={() => setShowPicker(!showPicker)}
                className="text-xs flex items-center gap-1 px-2 py-1 rounded-full hover-lift"
                style={{
                    color: 'var(--text-tertiary)',
                    background: showPicker ? 'var(--bg-tertiary)' : 'transparent',
                }}
            >
                <Smile className="w-3 h-3" />
                <span>{totalReactions === 0 ? 'React' : 'Add'}</span>
            </button>

            {/* Reaction Picker */}
            <AnimatePresence>
                {showPicker && (
                    <>
                        <motion.div
                            initial={{ opacity: 0, scale: 0.8, y: 10 }}
                            animate={{ opacity: 1, scale: 1, y: 0 }}
                            exit={{ opacity: 0, scale: 0.8, y: 10 }}
                            className="absolute bottom-full left-0 mb-2 p-2 rounded-2xl shadow-lg z-20 flex gap-1 glass-strong"
                        >
                            {QUICK_REACTIONS.map((emoji) => (
                                <motion.button
                                    key={emoji}
                                    whileHover={{ scale: 1.2 }}
                                    whileTap={{ scale: 0.9 }}
                                    onClick={() => handleReaction(emoji)}
                                    className="w-10 h-10 flex items-center justify-center text-2xl rounded-lg hover-lift"
                                    style={{ background: 'var(--bg-hover)' }}
                                >
                                    {emoji}
                                </motion.button>
                            ))}
                        </motion.div>

                        {/* Backdrop */}
                        <div
                            className="fixed inset-0 z-10"
                            onClick={() => setShowPicker(false)}
                        />
                    </>
                )}
            </AnimatePresence>
        </div>
    );
}
