import { useState, useEffect } from 'react'

function ProductionPlanningPage() {
  const [orders, setOrders] = useState([])
  const [expandedOrderId, setExpandedOrderId] = useState(null)
  const [feasibilityData, setFeasibilityData] = useState(null)
  const [errorMessage, setErrorMessage] = useState('')

  useEffect(() => {
    fetchOrders()
  }, [])

  function fetchOrders() {
    fetch('http://localhost:8080/api/orders')
      .then(response => response.json())
      .then(data => setOrders(data))
  }

  function handleStartProduction(orderId) {
    setErrorMessage('')
    fetch(`http://localhost:8080/api/orders/${orderId}/start-production`, {
      method: 'PUT'
    })
      .then(response => {
        if (!response.ok) {
          return response.text().then(text => { throw new Error(text) })
        }
        return response.json()
      })
      .then(() => fetchOrders())
      .catch(error => {
        setErrorMessage(error.message)
      })
  }

  function handleComplete(orderId) {
    setErrorMessage('')
    fetch(`http://localhost:8080/api/orders/${orderId}/complete`, {
      method: 'PUT'
    })
      .then(response => {
        if (!response.ok) {
          return response.text().then(text => { throw new Error(text) })
        }
        return response.json()
      })
      .then(() => fetchOrders())
      .catch(error => {
        setErrorMessage(error.message)
      })
  }

  function toggleDetails(order) {
    if (expandedOrderId === order.id) {
      setExpandedOrderId(null)
      setFeasibilityData(null)
      return
    }

    fetch('http://localhost:8080/api/feasibility/check', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ productId: order.product.id, quantityKg: order.quantityKg })
    })
      .then(response => response.json())
      .then(data => {
        setFeasibilityData(data)
        setExpandedOrderId(order.id)
      })
  }

  const relevantOrders = orders.filter(order => order.status !== 'SHIPPED')

  return (
    <div className="card">
      <h2>Üretim planlama</h2>

      {errorMessage && (
        <div style={{ background: '#fff3e0', color: '#b45309', padding: '0.75rem 1rem', borderRadius: '8px', marginBottom: '1rem' }}>
          ⚠️ {errorMessage}
        </div>
      )}

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
            <>
              <tr key={order.id}>
                <td>{order.product.name}</td>
                <td>{order.quantityKg} kg</td>
                <td>{order.customerName}</td>
                <td>{order.priority}</td>
                <td>{order.status}</td>
                <td>
                  <button onClick={() => toggleDetails(order)}>
                    {expandedOrderId === order.id ? 'Detayı Gizle' : 'Detay Göster'}
                  </button>
                  {order.status === 'PENDING' && (
                    <button onClick={() => handleStartProduction(order.id)}>Üretime Başlat</button>
                  )}
                  {order.status === 'IN_PRODUCTION' && (
                    <button onClick={() => handleComplete(order.id)}>Tamamlandı</button>
                  )}
                  {order.status === 'COMPLETED' && (
                    <div style={{ marginTop: '0.5rem', color: 'var(--text-muted)', fontSize: '0.85rem' }}>Depoda sevkiyat bekliyor</div>
                  )}
                </td>
              </tr>

              {expandedOrderId === order.id && feasibilityData && (
                <tr>
                  <td colSpan="6">
                    <div className="order-detail-panel">
                      <p>Üretim süresi: {feasibilityData.productionTimeHours} saat</p>
                      <p>Tahmini teslim: {feasibilityData.deliveryEstimateText}</p>

                      {feasibilityData.materialShortfalls.length > 0 ? (
                        <table border="1" cellPadding="6">
                          <thead>
                            <tr>
                              <th>Malzeme</th>
                              <th>Gereken</th>
                              <th>Fabrikada</th>
                              <th>Eksik</th>
                              <th>Tedarikçi</th>
                            </tr>
                          </thead>
                          <tbody>
                            {feasibilityData.materialShortfalls.map((item, index) => (
                              <tr key={index}>
                                <td>{item.materialName}</td>
                                <td>{item.requiredQuantity} {item.unit}</td>
                                <td>{item.factoryStock} {item.unit}</td>
                                <td>{item.shortfall} {item.unit}</td>
                                <td>{item.supplierName}</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      ) : (
                        <p>✅ Tüm malzemeler yeterli</p>
                      )}
                    </div>
                  </td>
                </tr>
              )}
            </>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default ProductionPlanningPage