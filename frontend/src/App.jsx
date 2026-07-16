import { useState } from 'react'
import './App.css'
import LoginPage from './LoginPage'
import InventoryPage from './InventoryPage'
import SalesPage from './SalesPage'

function App() {
  const [currentUser, setCurrentUser] = useState(null)

  function handleLoginSuccess(userData) {
    setCurrentUser(userData)
  }

  function handleLogout() {
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
      {currentUser.role === 'PLANNER' && <p>Üretim planlama ekranı güncelleniyor...</p>}
    </div>
  )
}

export default App