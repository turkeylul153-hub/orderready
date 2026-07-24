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

const roleIcons = {
  WAREHOUSE: '📦',
  SALES: '🧾',
  PLANNER: '🏭'
}

const rolePages = {
  WAREHOUSE: InventoryPage,
  SALES: SalesPage,
  PLANNER: ProductionPlanningPage
}

function App() {
  const [currentUser, setCurrentUser] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [activeRole, setActiveRole] = useState(null)

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
        const roles = data.role.split(',')
        setActiveRole(roles[0])
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
    const roles = userData.role.split(',')
    setActiveRole(roles[0])
  }

  function handleLogout() {
    localStorage.removeItem('token')
    setCurrentUser(null)
    setActiveRole(null)
  }

  if (isLoading) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Yükleniyor...</div>
  }

  if (!currentUser) {
    return <LoginPage onLoginSuccess={handleLoginSuccess} />
  }

  const userRoles = currentUser.role.split(',')
  const ActivePage = rolePages[activeRole]

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-logo">OrderReady</div>

        <nav className="sidebar-nav">
          {userRoles.map(role => (
            <div
              key={role}
              className={`sidebar-nav-item ${activeRole === role ? 'active' : ''}`}
              onClick={() => setActiveRole(role)}
            >
              <span className="sidebar-nav-icon">{roleIcons[role] || '📄'}</span>
              {roleLabels[role] || role}
            </div>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-user">
            <div className="sidebar-avatar">{currentUser.username.charAt(0).toUpperCase()}</div>
            <div>
              <div className="sidebar-username">{currentUser.username}</div>
              <span className="role-badge">{userRoles.join(' + ')}</span>
            </div>
          </div>
          <button className="secondary" onClick={handleLogout}>Çıkış yap</button>
        </div>
      </aside>

      <main className="app-content">
        {ActivePage && <ActivePage />}
      </main>
    </div>
  )
}

export default App