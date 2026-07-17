import { useState, useEffect } from 'react'

function ProductionPlanningPage() {
  const [orders, setOrders] = useState([])

  useEffect(() => {
    fetchOrders()
  }, [])

  function fetchOrders() {
    fetch('http://localhost:8080/api/orders')
      .then(response => response.json())
      .then(data => setOrders(data))
  }

  function handleStartProduction(orderId) {
    fetch(`http://localhost:8080/api/orders/${orderId}/start-production`, {
      method: 'PUT'
    })
      .then(response => response.json())
      .then(() => fetchOrders())
  }

  function handleComplete(orderId) {
    fetch(`http://localhost:8080/api/orders/${orderId}/complete`, {
      method: 'PUT'
    })
      .then(response => response.json())
      .then(() => fetchOrders())
  }

  // sadece planlamacının ilgilendiği durumları göster (gönderim depo işi)
  const relevantOrders = orders.filter(order => order.status !== 'SHIPPED')

  return (
    <div className="card">
      <h2>Üretim planlama</h2>
      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Ürün</th>
            <th>Miktar</th>
            <th>Müşteri</th>
            <th>Öncelik</th>
            <th>Durum</th>
            <th>İşlem</th>
          </tr>
        </thead>
        <tbody>
          {relevantOrders.map(order => (
            <tr key={order.id}>
              <td>{order.product.name}</td>
              <td>{order.quantityKg} kg</td>
              <td>{order.customerName}</td>
              <td>{order.priority}</td>
              <td>{order.status}</td>
              <td>
                {order.status === 'PENDING' && (
                  <button onClick={() => handleStartProduction(order.id)}>Üretime Başlat</button>
                )}
                {order.status === 'IN_PRODUCTION' && (
                  <button onClick={() => handleComplete(order.id)}>Tamamlandı</button>
                )}
                {order.status === 'COMPLETED' && (
                  <span>Depoda sevkiyat bekliyor</span>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default ProductionPlanningPage
