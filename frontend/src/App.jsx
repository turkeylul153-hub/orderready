import { useState } from 'react'
import './App.css'
import LoginPage from './LoginPage'
import InventoryPage from './InventoryPage'
import CalculatorPage from './CalculatorPage'

function App() {
  // giriş yapan kullanıcının bilgisini tutar (null ise henüz giriş yapılmamış demektir)
  const [currentUser, setCurrentUser] = useState(null)

  // LoginPage'den gelen "giriş başarılı" bilgisini işler
  function handleLoginSuccess(userData) {
    setCurrentUser(userData)
  }

  function handleLogout() {
    setCurrentUser(null)
  }

  // kullanıcı giriş yapmadıysa, sadece giriş ekranını göster
  if (!currentUser) {
    return <LoginPage onLoginSuccess={handleLoginSuccess} />
  }

  // giriş yapıldıysa, role göre doğru ekranı göster
  return (
    <div>
      <h1>OrderReady</h1>
      <p>Hoş geldin, {currentUser.username} ({currentUser.role})</p>
      <button onClick={handleLogout}>Çıkış yap</button>

      {currentUser.role === 'WAREHOUSE' && <InventoryPage />}
      {currentUser.role === 'PLANNER' && <CalculatorPage />}
      {currentUser.role === 'SUPPLIER' && <p>Tedarikçi ekranı yakında...</p>}
    </div>
  )
}

export default App