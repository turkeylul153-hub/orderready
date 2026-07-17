import { useState, useEffect } from 'react'

function SalesPage() {
  const [products, setProducts] = useState([])
  const [selectedProductId, setSelectedProductId] = useState('')
  const [quantity, setQuantity] = useState('')
  const [customerName, setCustomerName] = useState('')
  const [priority, setPriority] = useState('NORMAL')
  const [notes, setNotes] = useState('')
  const [orders, setOrders] = useState([])
  const [feasibility, setFeasibility] = useState(null)

  useEffect(() => {
    fetch('http://localhost:8080/api/products')
      .then(response => response.json())
      .then(data => setProducts(data))

    fetchOrders()
  }, [])

  // ürün veya miktar değiştikçe, otomatik olarak uygunluk kontrolü yap
  useEffect(() => {
    if (selectedProductId && quantity) {
      fetch('http://localhost:8080/api/feasibility/check', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ productId: selectedProductId, quantityKg: quantity })
      })
        .then(response => response.json())
        .then(data => setFeasibility(data))
    } else {
      setFeasibility(null)
    }
  }, [selectedProductId, quantity])

  function fetchOrders() {
    fetch('http://localhost:8080/api/orders')
      .then(response => response.json())
      .then(data => setOrders(data))
  }

  function handleCreateOrder() {
    fetch('http://localhost:8080/api/orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        product: { id: selectedProductId },
        quantityKg: quantity,
        customerName: customerName,
        priority: priority,
        notes: notes
      })
    })
      .then(response => response.json())
      .then(() => {
        setSelectedProductId('')
        setQuantity('')
        setCustomerName('')
        setPriority('NORMAL')
        setNotes('')
        setFeasibility(null)
        fetchOrders()
      })
  }

  return (
    <div className="card">
      <h2>Yeni sipariş oluştur</h2>

      <select value={selectedProductId} onChange={(e) => setSelectedProductId(e.target.value)}>
        <option value="">Ürün seçin</option>
        {products.map(product => (
          <option key={product.id} value={product.id}>{product.name}</option>
        ))}
      </select>

      <input
        type="number"
        placeholder="Miktar (kg)"
        value={quantity}
        onChange={(e) => setQuantity(e.target.value)}
      />

      <input
        type="text"
        placeholder="Müşteri adı"
        value={customerName}
        onChange={(e) => setCustomerName(e.target.value)}
      />

      <select value={priority} onChange={(e) => setPriority(e.target.value)}>
        <option value="NORMAL">Normal</option>
        <option value="URGENT">Acil</option>
      </select>

      <input
        type="text"
        placeholder="Not (opsiyonel)"
        value={notes}
        onChange={(e) => setNotes(e.target.value)}
      />

      {/* Otomatik uygunluk özeti */}
      {feasibility && (
        <div style={{ margin: '1rem 0', padding: '0.75rem', borderRadius: '8px', background: feasibility.hasShortfall ? '#fff3e0' : '#e8f5e9' }}>
          {feasibility.hasShortfall
            ? `⚠️ Bazı malzemeler eksik — Tahmini teslim: ${feasibility.deliveryEstimateText}`
            : `✅ Stok yeterli — Tahmini teslim: ${feasibility.deliveryEstimateText}`
          }
        </div>
      )}

      <button onClick={handleCreateOrder}>Sipariş Oluştur</button>

      <h3>Siparişlerim</h3>
      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Ürün</th>
            <th>Miktar</th>
            <th>Müşteri</th>
            <th>Öncelik</th>
            <th>Durum</th>
          </tr>
        </thead>
        <tbody>
          {orders.map(order => (
            <tr key={order.id}>
              <td>{order.product.name}</td>
              <td>{order.quantityKg} kg</td>
              <td>{order.customerName}</td>
              <td>{order.priority}</td>
              <td>{order.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default SalesPage
