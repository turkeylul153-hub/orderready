import { useState } from 'react'

// kullanıcının giriş yapmasını sağlar, başarılı olursa üst component'e bilgi verir
function LoginPage({ onLoginSuccess }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  function handleLogin() {
    fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Kullanıcı adı veya şifre hatalı')
        }
        return response.json()
      })
      .then(data => {
        setError('')
        // giriş başarılı, üst component'e kullanıcı bilgisini gönder
        onLoginSuccess(data)
      })
      .catch(() => {
        setError('Kullanıcı adı veya şifre hatalı')
      })
  }

  return (
    <div>
      <h2>Giriş yap</h2>
      <input
        type="text"
        placeholder="Kullanıcı adı"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <input
        type="password"
        placeholder="Şifre"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <button onClick={handleLogin}>Giriş yap</button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </div>
  )
}

export default LoginPage