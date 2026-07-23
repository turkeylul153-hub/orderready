import { useEffect } from 'react'

function Toast({ toast, onClose }) {
  useEffect(() => {
    if (toast) {
      const timer = setTimeout(() => {
        onClose()
      }, 3000)
      return () => clearTimeout(timer)
    }
  }, [toast])

  if (!toast) return null

  const styles = {
    success: { background: '#e8f5e9', color: '#1b5e20', icon: '✅' },
    error: { background: '#fee2e2', color: '#b91c1c', icon: '⚠️' }
  }

  const style = styles[toast.type] || styles.success

  return (
    <div style={{
      position: 'fixed',
      bottom: '2rem',
      right: '2rem',
      background: style.background,
      color: style.color,
      padding: '1rem 1.5rem',
      borderRadius: '10px',
      boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
      zIndex: 1000,
      fontSize: '0.9rem',
      fontWeight: 600
    }}>
      {style.icon} {toast.message}
    </div>
  )
}

export default Toast