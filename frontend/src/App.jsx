import { useState, useEffect } from 'react'
import './App.css'
import LoginPage from './LoginPage'
import InventoryPage from './InventoryPage'
import SalesPage from './SalesPage'
import ProductionPlanningPage from './ProductionPlanningPage'
import UserManagementPage from './UserManagementPage'
import { authFetch } from './api'

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

const allRoles = ['WAREHOUSE', 'SALES', 'PLANNER', 'ADMIN']

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

  function handleRequestAccess(role) {
    const confirmed = window.confirm(`"${roleLabels[role]}" için erişim talep etmek istediğinize emin misiniz?`)
    if (!confirmed) return

    authFetch('http://localhost:8080/api/access-requests', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: currentUser.id, requestedRole: role })
    })
      .then(response => response.json())
      .then(() => {
        alert('Talebiniz gönderildi, yönetici onayı bekleniyor.')
      })
      .catch(() => {
        alert('Talep gönderilemedi.')
      })
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
        {allRoles.map(role => {
          const owned = userRoles.includes(role)

          if (owned) {
            return (
              <div
                key={role}
                className={`nav-card ${activeRole === role ? 'active' : ''}`}
                onClick={() => setActiveRole(role)}
              >
                <span className="nav-card-icon">{roleIcons[role]}</span>
                <div>
                  <div className="nav-card-title">{roleLabels[role]}</div>
                  <div className="nav-card-subtitle">{activeRole === role ? 'Şu an aktif' : 'Geçmek için tıkla'}</div>
                </div>
              </div>
            )
          }

          return (
            <div
              key={role}
              className="nav-card"
              style={{ opacity: 0.5, cursor: 'pointer' }}
              onClick={() => handleRequestAccess(role)}
            >
              <span className="nav-card-icon">🔒</span>
              <div>
                <div className="nav-card-title">{roleLabels[role]}</div>
                <div className="nav-card-subtitle">Erişim talep et</div>
              </div>
            </div>
          )
        })}
      </div>

      <main className="app-content-cards">
        {ActivePage && <ActivePage />}
      </main>
    </div>
  )
}

export default App