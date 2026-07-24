import { useState, useEffect } from 'react'
import Toast from './Toast'

function InventoryPage() {
  const [stock, setStock] = useState([])
  const [amounts, setAmounts] = useState({})
  const [pendingShipments, setPendingShipments] = useState([])
  const [shippedOrders, setShippedOrders] = useState([])
  const [history, setHistory] = useState([])
  const [historyMaterialFilter, setHistoryMaterialFilter] = useState('')
  const [historyTypeFilter, setHistoryTypeFilter] = useState('ALL')
  const [showShippedHistory, setShowShippedHistory] = useState(false)
  const [showStockHistory, setShowStockHistory] = useState(false)
  const [toast, setToast] = useState(null)
  const [historyPage, setHistoryPage] = useState(0)
  const [historyTotalPages, setHistoryTotalPages] = useState(0)

  useEffect(() => {
    fetchStock()
    fetchOrders()
  }, [])

  useEffect(() => {
    if (showStockHistory) {
      fetchHistory()
    }
  }, [historyPage])

  function fetchStock() {
    fetch('http://localhost:8080/api/material-stock')
      .then(response => response.json())
      .then(data => setStock(data))
  }

  function fetchOrders() {
    fetch('http://localhost:8080/api/orders?page=0&size=100')
      .then(response => response.json())
      .then(data => {
        setPendingShipments(data.content.filter(order => order.status === 'COMPLETED'))
        setShippedOrders(data.content.filter(order => order.status === 'SHIPPED'))
      })
  }

  function fetchHistory() {
    fetch(`http://localhost:8080/api/material-stock/history?page=${historyPage}&size=10`)
      .then(response => response.json())
      .then(data => {
        setHistory(data.content)
        setHistoryTotalPages(data.totalPages)
      })
  }

  function toggleStockHistory() {
    if (!showStockHistory) {
      fetchHistory()
    }
    setShowStockHistory(!showStockHistory)
  }

  function handleAmountChange(materialId, value) {
    setAmounts({ ...amounts, [materialId]: value })
  }

  function handleAdjust(materialId, type) {
    const quantity = amounts[materialId]

    if (!quantity) {
      setToast({ message: 'Lütfen bir miktar girin', type: 'error' })
      return
    }

    fetch(`http://localhost:8080/api/material-stock/${materialId}/adjust`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ quantity: quantity, type: type })
    })
      .then(response => response.json())
      .then(() => {
        setToast({ message: 'Stok güncellendi', type: 'success' })
        setAmounts({ ...amounts, [materialId]: '' })
        fetchStock()
      })
      .catch(error => setToast({ message: 'İşlem başarısız oldu', type: 'error' }))
  }

  function handleShip(order) {
    const confirmed = window.confirm(
      `${order.product.name} - ${order.quantityKg} kg (${order.customerName}) için gönderimi onaylıyor musunuz?`
    )
    if (!confirmed) return

    fetch(`http://localhost:8080/api/orders/${order.id}/ship`, {
      method: 'PUT'
    })
      .then(response => response.json())
      .then(() => {
        setToast({ message: 'Sipariş gönderildi', type: 'success' })
        fetchOrders()
      })
      .catch(() => setToast({ message: 'Gönderim başarısız oldu', type: 'error' }))
  }

  function handleRevertShipment(order) {
    const pin = window.prompt('Bu gönderimi geri almak için yetkili PIN kodunu girin:')
    if (!pin) return

    fetch(`http://localhost:8080/api/orders/${order.id}/revert-shipment`, {
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
      .then(() => {
        setToast({ message: 'Gönderim geri alındı', type: 'success' })
        fetchOrders()
      })
      .catch(error => setToast({ message: error.message, type: 'error' }))
  }

  let filteredHistory = history

  if (historyTypeFilter !== 'ALL') {
    filteredHistory = filteredHistory.filter(tx => tx.type === historyTypeFilter)
  }

  if (historyMaterialFilter.trim() !== '') {
    const search = historyMaterialFilter.toLowerCase()
    filteredHistory = filteredHistory.filter(tx => tx.material.name.toLowerCase().includes(search))
  }

  return (
    <div>
      <Toast toast={toast} onClose={() => setToast(null)} />

      <div className="card">
        <h2>Sevkiyat bekleyen siparişler</h2>
        {pendingShipments.length === 0 ? (
          <div className="empty-state">📦 Şu an sevkiyat bekleyen sipariş yok.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Ürün</th>
                <th>Miktar</th>
                <th>Müşteri</th>
                <th>İşlem</th>
              </tr>
            </thead>
            <tbody>
              {pendingShipments.map(order => (
                <tr key={order.id}>
                  <td>{order.product.name}</td>
                  <td>{order.quantityKg} kg</td>
                  <td>{order.customerName}</td>
                  <td>
                    <button onClick={() => handleShip(order)}>Gönderildi</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <div className="card">
        <h2>Depo stok yönetimi</h2>

        <table>
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

      <div className="card">
        <h2 onClick={toggleStockHistory} style={{ cursor: 'pointer' }}>
          Stok Hareketleri Geçmişi {showStockHistory ? '▲' : '▼'}
        </h2>
        {showStockHistory && (
          <div>
            <div style={{ marginBottom: '1rem' }}>
              <input
                type="text"
                placeholder="Malzeme ara..."
                value={historyMaterialFilter}
                onChange={(e) => setHistoryMaterialFilter(e.target.value)}
              />
              <select value={historyTypeFilter} onChange={(e) => setHistoryTypeFilter(e.target.value)}>
                <option value="ALL">Tüm türler</option>
                <option value="INITIAL">Başlangıç</option>
                <option value="ADDITION">Ekleme</option>
                <option value="REMOVAL">Çıkarma</option>
                <option value="ORDER_CONSUMPTION">Sipariş Tüketimi</option>
                <option value="ORDER_REVERSAL">Geri Alma</option>
              </select>
            </div>

            {filteredHistory.length === 0 ? (
              <div className="empty-state">📜 Kayıt bulunamadı.</div>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>Malzeme</th>
                    <th>Değişim</th>
                    <th>Yeni Bakiye</th>
                    <th>Tür</th>
                    <th>Tarih</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredHistory.map(tx => (
                    <tr key={tx.id}>
                      <td>{tx.material.name}</td>
                      <td>{tx.quantityChange > 0 ? '+' : ''}{tx.quantityChange} {tx.material.unit}</td>
                      <td>{tx.balanceAfter} {tx.material.unit}</td>
                      <td>{tx.type}</td>
                      <td>{tx.createdAt}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}

            {historyTotalPages > 1 && (
              <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem', alignItems: 'center' }}>
                <button
                  className="secondary"
                  disabled={historyPage === 0}
                  onClick={() => setHistoryPage(historyPage - 1)}
                >
                  ← Önceki
                </button>
                <span>Sayfa {historyPage + 1} / {historyTotalPages}</span>
                <button
                  className="secondary"
                  disabled={historyPage >= historyTotalPages - 1}
                  onClick={() => setHistoryPage(historyPage + 1)}
                >
                  Sonraki →
                </button>
              </div>
            )}
          </div>
        )}
      </div>

      <div className="card">
        <h2 onClick={() => setShowShippedHistory(!showShippedHistory)} style={{ cursor: 'pointer' }}>
          Gönderilmiş Siparişler (Geçmiş) {showShippedHistory ? '▲' : '▼'}
        </h2>
        {showShippedHistory && (
          shippedOrders.length === 0 ? (
            <div className="empty-state">📦 Henüz gönderilmiş sipariş yok.</div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Ürün</th>
                  <th>Miktar</th>
                  <th>Müşteri</th>
                  <th>İşlem</th>
                </tr>
              </thead>
              <tbody>
                {shippedOrders.map(order => (
                  <tr key={order.id}>
                    <td>{order.product.name}</td>
                    <td>{order.quantityKg} kg</td>
                    <td>{order.customerName}</td>
                    <td>
                      <button className="secondary" onClick={() => handleRevertShipment(order)}>Geri Al</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )
        )}
      </div>
    </div>
  )
}

export default InventoryPage