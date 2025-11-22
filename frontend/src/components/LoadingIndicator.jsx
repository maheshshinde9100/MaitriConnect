export default function LoadingIndicator() {
  return (
    <div style={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      minHeight: '100vh',
      background: 'var(--bg-app)'
    }}>
      <div style={{ textAlign: 'center' }}>
        <div className="animate-spin" style={{
          width: '48px',
          height: '48px',
          border: '4px solid var(--bg-tertiary)',
          borderTopColor: 'var(--primary-500)',
          borderRadius: 'var(--radius-full)',
          margin: '0 auto var(--space-4)'
        }}></div>
        <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>
          Loading...
        </p>
      </div>
    </div>
  );
}
