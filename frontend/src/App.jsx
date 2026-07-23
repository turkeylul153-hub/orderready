import { useState, useEffect } from 'react'
import './App.css'
import LoginPage from './LoginPage'
import InventoryPage from './InventoryPage'
import SalesPage from './SalesPage'
import ProductionPlanningPage from './ProductionPlanningPage'

function App() {
  const [currentUser, setCurrentUser] = useState(null)
  const [isLoading, setIsLoading] = useState(true)

  // Sayfa ilk açıldığında, kayıtlı bir token var mı diye backend'e sor
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