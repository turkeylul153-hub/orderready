import { useState, useEffect } from 'react'

function InventoryPage() {
  // backendden gelen stok listesini tutar
  const [inventory, setInventory] = useState([])

  // kullanıcının input kutularına yazdığı, henüz kaydedilmemiş değerleri tutar
  const [edits, setEdits] = useState({})

  // sayfa ilk açıldığında bir kere stok listesini çeker
  useEffect(() => {
    fetchInventory()
  }, [])

  // backendden güncel stok listesini çeker
  function fetchInventory() {
    fetch('http://localhost:8080/api/inventory')
      .then(response => response.json())
      .then(data => setInventory(data))
  }

  // kullanıcı bir input kutusuna yazı yazdığında çalışır, edits state'ini günceller
  function handleInputChange(id, value) {
    setEdits({ ...edits, [id]: value })
  }

  // "güncelle" butonuna basılınca çalışır, backend'e PUT isteği gönderir
  function handleUpdate(id) {
    const newQuantity = edits[id]
    fetch(`http://localhost:8080/api/inventory/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ quantity: newQuantity })
    })
      .then(response => response.json())
      .then(() => {
        // güncelleme başarılı olunca listeyi baştan çekip ekranı tazele
        fetchInventory()
      })
  }

  return (
    <div className="card">
      <h2>Depo stok güncelleme</h2>
      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Malzeme</th>
            <th>Mevcut stok</th>
            <th>Yeni miktar</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {/* Her stok kaydı için bir satır oluştur */}
          {inventory.map(item => (
            <tr key={item.id}>
              <td>{item.material.name}</td>
              <td>{item.currentQuantity} {item.material.unit}</td>
              <td>
                <input
                  type="number"
                  placeholder={item.currentQuantity}
                  value={edits[item.id] ?? ''}
                  onChange={(e) => handleInputChange(item.id, e.target.value)}
                />
              </td>
              <td>
                <button onClick={() => handleUpdate(item.id)}>Güncelle</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default InventoryPage
