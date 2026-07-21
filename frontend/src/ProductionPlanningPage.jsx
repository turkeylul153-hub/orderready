import { useState, useEffect } from 'react'
import ManagementPanel from './ManagementPanel'

function ProductionPlanningPage() {
  const [orders, setOrders] = useState([])
  const [expandedOrderId, setExpandedOrderId] = useState(null)
  const [feasibilityData, setFeasibilityData] = useState(null)
  const [errorMessage, setErrorMessage] = useState('')
  const [statusFilter, setStatusFilter] = useState('ALL')
  const [searchText, setSearchText] = useState('')

  useEffect(() => {
    fetchOrders()
  }, [])

  function fetchOrders() {
    fetch('http://localhost:8080/api/orders')
      .then(response => response.json())
      .then(data => setOrders(data))
  }

  function handleStartProduction(orderId) {
    const confirmed = window.confirm('Bu siparişin üretimine başlamak istediğinize emin misiniz?')
    if (!confirmed) return

    setErrorMessage('')
    fetch(`http://localhost:8080/api/orders/${orderId}/start-production`, {
      method: 'PUT'
    })
      .then(response => {
        if (!response.ok) {
          return response.json().then(data => {
            throw new Error(data.message || 'Bilinmeyen bir hata oluştu')
          })
        }
        return response.json()
      })
      .then(() => fetchOrders())
      .catch(error => setErrorMessage(error.message))
  }

  function handleComplete(orderId) {
    const confirmed = window.confirm('Bu siparişin üretimini tamamlandı olarak işaretlemek istediğinize emin misiniz?')
    if (!confirmed) return

    setErrorMessage('')
    fetch(`http://localhost:8080/api/orders/${orderId}/complete`, {
      method: 'PUT'
    })
      .then(response => {
        if (!response.ok) {
          return response.json().then(data => {
            throw new Error(data.message || 'Bilinmeyen bir hata oluştu')
          })
        }
        return response.json()
      })
      .then(() => fetchOrders())
      .catch(error => setErrorMessage(error.message))
  }

  function handleRevertProduction(orderId) {
    const pin = window.prompt('Bu işlemi geri almak için yetkili PIN kodunu girin:')
    if (!pin) return

    setErrorMessage('')
    fetch(`http://localhost:8080/api/orders/${orderId}/revert-production`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ pin: pin })
    })
      .then(response => {
        if (!response.ok) {
          return response.json().then(data => {
            throw new Error(data.message || 'Bilinmeyen bir hata oluştu')
          })
        }
        return response.json()
      })
      .then(() => fetchOrders())
      .catch(error => setErrorMessage(error.message))
  }
function handleDeleteOrder(orderId) {
  const confirmed = window.confirm('Bu siparişi tamamen silmek istediğinize emin misiniz? Bu işlem geri alınamaz.')
  if (!confirmed) return

  setErrorMessage('')
  fetch(`http://localhost:8080/api/orders/${orderId}`, {
    method: 'DELETE'
  })
    .then(response => {
      if (!response.ok) {
        return response.json().then(data => {
          throw new Error(data.message || 'Bilinmeyen bir hata oluştu')
        })
      }
      fetchOrders()
    })
    .catch(error => setErrorMessage(error.message))
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

  const notShipped = orders.filter(order => order.status !== 'SHIPPED')

  const pendingCount = notShipped.filter(o => o.status === 'PENDING').length
  const inProductionCount = notShipped.filter(o => o.status === 'IN_PRODUCTION').length
  const completedCount = notShipped.filter(o => o.status === 'COMPLETED').length

  let filteredOrders = notShipped
  if (statusFilter !== 'ALL') {
    filteredOrders = filteredOrders.filter(order => order.status === statusFilter)
  }

  if (searchText.trim() !== '') {
    const search = searchText.toLowerCase()
    filteredOrders = filteredOrders.filter(order =>
      order.customerName?.toLowerCase().includes(search) ||
      order.product.name.toLowerCase().includes(search)
    )
  }

  const sortedOrders = [...filteredOrders].sort((a, b) => {
    if (a.priority === b.priority) return 0
    return a.priority === 'URGENT' ? -1 : 1
  })

  return (
    <div className="card">
      <h2>Üretim planlama</h2>

      <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem' }}>
        <div style={{ background: 'var(--green-light)', padding: '0.75rem 1.25rem', borderRadius: '10px' }}>
          <strong>{pendingCount}</strong> Bekleyen
        </div>
        <div style={{ background: 'var(--green-light)', padding: '0.75rem 1.25rem', borderRadius: '10px' }}>
          <strong>{inProductionCount}</strong> Üretimde
        </div>
        <div style={{ background: 'var(--green-light)', padding: '0.75rem 1.25rem', borderRadius: '10px' }}>
          <strong>{completedCount}</strong> Sevkiyat Bekliyor
        </div>
      </div>

      {errorMessage && (
        <div style={{ background: '#fff3e0', color: '#b45309', padding: '0.75rem 1rem', borderRadius: '8px', marginBottom: '1rem' }}>
          ⚠️ {errorMessage}
        </div>
      )}

      <div style={{ marginBottom: '1rem' }}>
        <button className={statusFilter === 'ALL' ? '' : 'secondary'} onClick={() => setStatusFilter('ALL')}>Tümü</button>
        <button className={statusFilter === 'PENDING' ? '' : 'secondary'} onClick={() => setStatusFilter('PENDING')}>Bekleyen</button>
        <button className={statusFilter === 'IN_PRODUCTION' ? '' : 'secondary'} onClick={() => setStatusFilter('IN_PRODUCTION')}>Üretimde</button>
        <button className={statusFilter === 'COMPLETED' ? '' : 'secondary'} onClick={() => setStatusFilter('COMPLETED')}>Sevkiyat Bekleyen</button>
        <input
          type="text"
          placeholder="Müşteri veya ürün ara..."
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
        />
      </div>

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Ürün</th>
            <th>Miktar</th>
            <th>Müşteri</th>
            <th>Öncelik</th>
            <th>Not</th>
            <th>Durum</th>
            <th>İşlem</th>
          </tr>
        </thead>
        <tbody>
          {sortedOrders.map(order => (
            <>
              <tr key={order.id}>
                <td>{order.product.name}</td>
                <td>{order.quantityKg} kg</td>
                <td>{order.customerName}</td>
                <td>{order.priority}</td>
                <td>{order.notes || '-'}</td>
                <td>{order.status}</td>
                <td>
                  <button onClick={() => toggleDetails(order)}>
                    {expandedOrderId === order.id ? 'Detayı Gizle' : 'Detay Göster'}
                  </button>
                  {order.status === 'PENDING' && (
                    <>
                      <button onClick={() => handleStartProduction(order.id)}>Üretime Başlat</button>
                      <button className="secondary" onClick={() => handleDeleteOrder(order.id)}>Sil</button>
                    </>
                  )}
                  {order.status === 'IN_PRODUCTION' && (
                    <>
                      <button onClick={() => handleComplete(order.id)}>Tamamlandı</button>
                      <button className="secondary" onClick={() => handleRevertProduction(order.id)}>Geri Al</button>
                    </>
                  )}
                  {order.status === 'COMPLETED' && (
                    <div style={{ marginTop: '0.5rem', color: 'var(--text-muted)', fontSize: '0.85rem' }}>Depoda sevkiyat bekliyor</div>
                  )}
                </td>
              </tr>

              {expandedOrderId === order.id && feasibilityData && (
                <tr>
                  <td colSpan="7">
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

      <ManagementPanel />
    </div>
  )
}

export default ProductionPlanningPage
