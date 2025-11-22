import { motion } from "framer-motion";

export default function MessageReactions({ reactions, messageId, onReact, currentUserId }) {
    if (!reactions || Object.keys(reactions).length === 0) return null;

    return (
        <div style={{
            position: 'absolute',
            bottom: '-10px',
            right: 'var(--space-2)',
            display: 'flex',
            gap: 'var(--space-1)',
            flexWrap: 'wrap',
        }}>
            {Object.entries(reactions).map(([emoji, count]) => (
                <motion.button
                    key={emoji}
                    whileHover={{ scale: 1.1 }}
                    whileTap={{ scale: 0.95 }}
                    onClick={() => onReact(messageId, emoji)}
                    style={{
                        padding: '2px 6px',
                        background: 'var(--bg-primary)',
                        border: '1px solid var(--border-primary)',
                        borderRadius: 'var(--radius-full)',
                        fontSize: '12px',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '4px',
                    }}
                >
                    <span>{emoji}</span>
                    <span style={{ fontSize: '10px', color: 'var(--text-tertiary)' }}>{count}</span>
                </motion.button>
            ))}
        </div>
    );
}
