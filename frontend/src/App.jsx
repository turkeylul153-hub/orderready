import { useState } from 'react'
import './App.css'
import LoginPage from './LoginPage'
import InventoryPage from './InventoryPage'
import SalesPage from './SalesPage'
import ProductionPlanningPage from './ProductionPlanningPage'

function App() {
  const [currentUser, setCurrentUser] = useState(() => {
      const saved = localStorage.getItem('currentUser')
      // saved bir string olarak gelir ama biz object istiyoruz
      // JSON.parse(saved) metni objecte çevirir.
      return saved ? JSON.parse(saved) : null
      })

  function handleLoginSuccess(userData) {
      localStorage.setItem('currentUser' , JSON.stringify(userData))

    setCurrentUser(userData)
  }

  function handleLogout() {
      localStorage.removeItem('currentUser')
    setCurrentUser(null)
  }

  if (!currentUser) {
    return <LoginPage onLoginSuccess={handleLoginSuccess} />
  }

  return (
    <div>
      <div className="app-header">
        <h1>OrderReady</h1>
        <div className="user-info">
          <span>{currentUser.username}</span>
          <span className="role-badge">{currentUser.role}</span>
          <button className="secondary" onClick={handleLogout}>Çıkış yap</button>
        </div>
      </div>

      {currentUser.role === 'WAREHOUSE' && <InventoryPage />}
      {currentUser.role === 'SALES' && <SalesPage />}
    {currentUser.role === 'PLANNER' && <ProductionPlanningPage />}
    </div>
  )
}

export default App

