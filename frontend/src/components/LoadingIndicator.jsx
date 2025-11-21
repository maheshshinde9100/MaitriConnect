import { motion } from 'framer-motion';

export default function LoadingIndicator() {
  return (
    <div
      className="flex items-center justify-center min-h-screen"
      style={{ background: 'var(--bg-primary)' }}
    >
      <div className="text-center">
        {/* Animated Logo */}
        <motion.div
          className="w-20 h-20 mx-auto mb-6 rounded-2xl gradient-primary flex items-center justify-center"
          animate={{
            scale: [1, 1.1, 1],
            rotate: [0, 5, -5, 0],
          }}
          transition={{
            duration: 2,
            repeat: Infinity,
            ease: "easeInOut",
          }}
        >
          <span className="text-3xl font-bold text-white">M</span>
        </motion.div>

        {/* Loading Text */}
        <h2 className="text-xl font-semibold mb-3 gradient-text">
          MaitriConnect
        </h2>

        {/* Loading Dots */}
        <div className="flex items-center justify-center gap-2">
          {[0, 1, 2].map((i) => (
            <motion.div
              key={i}
              className="w-2 h-2 rounded-full"
              style={{ background: 'var(--accent-primary)' }}
              animate={{
                y: [0, -10, 0],
                opacity: [0.5, 1, 0.5],
              }}
              transition={{
                duration: 1,
                repeat: Infinity,
                delay: i * 0.2,
              }}
            />
          ))}
        </div>
      </div>
    </div>
  );
}
