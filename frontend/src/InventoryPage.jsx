import { useState, useEffect } from 'react'

function InventoryPage() {
  const [stock, setStock] = useState([])
  const [amounts, setAmounts] = useState({})
  const [errorMessage, setErrorMessage] = useState('')

  useEffect(() => {
    fetchStock()
  }, [])

  function fetchStock() {
    fetch('http://localhost:8080/api/material-stock')
      .then(response => response.json())
      .then(data => setStock(data))
  }

  function handleAmountChange(materialId, value) {
    setAmounts({ ...amounts, [materialId]: value })
  }

  function handleAdjust(materialId, type) {
    setErrorMessage('')
    const quantity = amounts[materialId]

    if (!quantity) {
      setErrorMessage('Lütfen bir miktar girin')
      return
    }

    fetch(`http://localhost:8080/api/material-stock/${materialId}/adjust`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ quantity: quantity, type: type })
    })
      .then(response => response.json())
      .then(() => {
        setAmounts({ ...amounts, [materialId]: '' })
        fetchStock()
      })
      .catch(error => setErrorMessage('İşlem başarısız oldu'))
  }

  return (
    <div className="card">
      <h2>Depo stok yönetimi</h2>

      {errorMessage && (
        <div style={{ background: '#fff3e0', color: '#b45309', padding: '0.75rem 1rem', borderRadius: '8px', marginBottom: '1rem' }}>
          ⚠️ {errorMessage}
        </div>
      )}

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Malzeme</th>
            <th>Mevcut Stok</th>
            <th>Miktar</th>
            <th>İşlem</th>
          </tr>
        </thead>
        <tbody>
          {stock.map(item => (
            <tr key={item.material.id}>
              <td>{item.material.name}</td>
              <td>{item.balanceAfter} {item.material.unit}</td>
              <td>
                <input
                  type="number"
                  placeholder="Miktar"
                  value={amounts[item.material.id] || ''}
                  onChange={(e) => handleAmountChange(item.material.id, e.target.value)}
                />
              </td>
              <td>
                <button onClick={() => handleAdjust(item.material.id, 'ADDITION')}>Ekle</button>
                <button className="secondary" onClick={() => handleAdjust(item.material.id, 'REMOVAL')}>Çıkar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default InventoryPage