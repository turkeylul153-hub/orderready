import { useState, useEffect } from 'react'
import './App.css'
import LoginPage from './LoginPage'
import InventoryPage from './InventoryPage'
import SalesPage from './SalesPage'
import ProductionPlanningPage from './ProductionPlanningPage'

const roleLabels = {
  WAREHOUSE: 'Depo Yönetimi',
  SALES: 'Sipariş Oluşturma',
  PLANNER: 'Üretim Planlama'
}

function App() {
  const [currentUser, setCurrentUser] = useState(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('token')

    if (!token) {
      setIsLoading(false)
      return
    }

    fetch(`http://localhost:8080/api/auth/me?token=${token}`)
      .then(response => {
        if (!response.ok) {
          throw new Error('Geçersiz oturum')
        }
        return response.json()
      })
      .then(data => {
        setCurrentUser(data)
        setIsLoading(false)
      })
      .catch(() => {
        localStorage.removeItem('token')
        setIsLoading(false)
      })
  }, [])

  function handleLoginSuccess(userData) {
    localStorage.setItem('token', userData.token)
    setCurrentUser(userData)
  }

  function handleLogout() {
    localStorage.removeItem('token')
    setCurrentUser(null)
  }

  if (isLoading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Yükleniyor...</div>
  }

  if (!currentUser) {
    return <LoginPage onLoginSuccess={handleLoginSuccess} />
  }

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-logo">OrderReady</div>

        <nav className="sidebar-nav">
          <div className="sidebar-nav-item active">
            {roleLabels[currentUser.role] || 'Ana Sayfa'}
          </div>
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-user">
            <span className="sidebar-username">{currentUser.username}</span>
            <span className="role-badge">{currentUser.role}</span>
          </div>
          <button className="secondary" onClick={handleLogout}>Çıkış yap</button>
        </div>
      </aside>

      <main className="app-content">
        {currentUser.role === 'WAREHOUSE' && <InventoryPage />}
        {currentUser.role === 'SALES' && <SalesPage />}
        {currentUser.role === 'PLANNER' && <ProductionPlanningPage />}
      </main>
    </div>
  )
}

export default App