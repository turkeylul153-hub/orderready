import { useState, useEffect } from 'react'
import './App.css'
import LoginPage from './LoginPage'
import InventoryPage from './InventoryPage'
import SalesPage from './SalesPage'
import ProductionPlanningPage from './ProductionPlanningPage'
import UserManagementPage from './UserManagementPage'

const roleLabels = {
  WAREHOUSE: 'Depo Yönetimi',
  SALES: 'Sipariş Oluşturma',
  PLANNER: 'Üretim Planlama',
  ADMIN: 'Kullanıcı Yönetimi'
}

const roleIcons = {
  WAREHOUSE: '📦',
  SALES: '🧾',
  PLANNER: '🏭',
  ADMIN: '⚙️'
}

const rolePages = {
  WAREHOUSE: InventoryPage,
  SALES: SalesPage,
  PLANNER: ProductionPlanningPage,
  ADMIN: UserManagementPage
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
    <div className="app-with-cards">
      <div className="app-header">
        <h1>OrderReady</h1>
        <div className="user-info">
          <span>{currentUser.username}</span>
          <span className="role-badge">{userRoles.join(' + ')}</span>
          <button className="secondary" onClick={handleLogout}>Çıkış yap</button>
        </div>
      </div>

      <div className="nav-cards">
        {userRoles.map(role => (
          <div
            key={role}
            className={`nav-card ${activeRole === role ? 'active' : ''}`}
            onClick={() => setActiveRole(role)}
          >
            <span className="nav-card-icon">{roleIcons[role] || '📄'}</span>
            <div>
              <div className="nav-card-title">{roleLabels[role] || role}</div>
              <div className="nav-card-subtitle">{activeRole === role ? 'Şu an aktif' : 'Geçmek için tıkla'}</div>
            </div>
          </div>
        ))}
      </div>

      <main className="app-content-cards">
        {ActivePage && <ActivePage />}
      </main>
    </div>
  )
}

export default App