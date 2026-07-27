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

  const [pendingPage, setPendingPage] = useState(0)
  const [pendingTotalPages, setPendingTotalPages] = useState(0)

  const [shippedPage, setShippedPage] = useState(0)
  const [shippedTotalPages, setShippedTotalPages] = useState(0)

  useEffect(() => {
    fetchStock()
  }, [])

  useEffect(() => {
    fetchPendingShipments()
  }, [pendingPage])

  useEffect(() => {
    if (showShippedHistory) {
      fetchShippedOrders()
    }
  }, [shippedPage, showShippedHistory])

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

  function fetchPendingShipments() {
    fetch(`http://localhost:8080/api/orders?status=COMPLETED&page=${pendingPage}&size=5`)
      .then(response => response.json())
      .then(data => {
        setPendingShipments(data.content)
        setPendingTotalPages(data.totalPages)
      })
  }

  function fetchShippedOrders() {
    fetch(`http://localhost:8080/api/orders?status=SHIPPED&page=${shippedPage}&size=5`)
      .then(response => response.json())
      .then(data => {
        setShippedOrders(data.content)
        setShippedTotalPages(data.totalPages)
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

  function toggleShippedHistory() {
    if (!showShippedHistory) {
      fetchShippedOrders()
    }
    setShowShippedHistory(!showShippedHistory)
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
        fetchPendingShipments()
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
        fetchShippedOrders()
        fetchPendingShipments()
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
          <>
            <div className="table-scroll">
              <table className="responsive-table">
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
                      <td data-label="Ürün">{order.product.name}</td>
                      <td data-label="Miktar">{order.quantityKg} kg</td>
                      <td data-label="Müşteri">{order.customerName}</td>
                      <td data-label="İşlem">
                        <button onClick={() => handleShip(order)}>Gönderildi</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {pendingTotalPages > 1 && (
              <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
                <button className="secondary" disabled={pendingPage === 0} onClick={() => setPendingPage(pendingPage - 1)}>← Önceki</button>
                <span>Sayfa {pendingPage + 1} / {pendingTotalPages}</span>
                <button className="secondary" disabled={pendingPage >= pendingTotalPages - 1} onClick={() => setPendingPage(pendingPage + 1)}>Sonraki →</button>
              </div>
            )}
          </>
        )}
      </div>

      <div className="card">
        <h2>Depo stok yönetimi</h2>

        <div className="table-scroll">
          <table className="responsive-table">
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
                  <td data-label="Malzeme">{item.material.name}</td>
                  <td data-label="Mevcut Stok">{item.balanceAfter} {item.material.unit}</td>
                  <td data-label="Miktar">
                    <input
                      type="number"
                      placeholder="Miktar"
                      value={amounts[item.material.id] || ''}
                      onChange={(e) => handleAmountChange(item.material.id, e.target.value)}
                    />
                  </td>
                  <td data-label="İşlem">
                    <button onClick={() => handleAdjust(item.material.id, 'ADDITION')}>Ekle</button>
                    <button className="secondary" onClick={() => handleAdjust(item.material.id, 'REMOVAL')}>Çıkar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="card">
        <h2 onClick={toggleStockHistory} style={{ cursor: 'pointer' }}>
          Stok Hareketleri Geçmişi {showStockHistory ? '▲' : '▼'}
        </h2>
        {showStockHistory && (
          <div>
            <div style={{ marginBottom: '1rem', display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
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
              <div className="table-scroll">
                <table className="responsive-table">
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
                        <td data-label="Malzeme">{tx.material.name}</td>
                        <td data-label="Değişim">{tx.quantityChange > 0 ? '+' : ''}{tx.quantityChange} {tx.material.unit}</td>
                        <td data-label="Yeni Bakiye">{tx.balanceAfter} {tx.material.unit}</td>
                        <td data-label="Tür">{tx.type}</td>
                        <td data-label="Tarih">{tx.createdAt}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {historyTotalPages > 1 && (
              <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
                <button className="secondary" disabled={historyPage === 0} onClick={() => setHistoryPage(historyPage - 1)}>← Önceki</button>
                <span>Sayfa {historyPage + 1} / {historyTotalPages}</span>
                <button className="secondary" disabled={historyPage >= historyTotalPages - 1} onClick={() => setHistoryPage(historyPage + 1)}>Sonraki →</button>
              </div>
            )}
          </div>
        )}
      </div>

      <div className="card">
        <h2 onClick={toggleShippedHistory} style={{ cursor: 'pointer' }}>
          Gönderilmiş Siparişler (Geçmiş) {showShippedHistory ? '▲' : '▼'}
        </h2>
        {showShippedHistory && (
          shippedOrders.length === 0 ? (
            <div className="empty-state">📦 Henüz gönderilmiş sipariş yok.</div>
          ) : (
            <>
              <div className="table-scroll">
                <table className="responsive-table">
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
                        <td data-label="Ürün">{order.product.name}</td>
                        <td data-label="Miktar">{order.quantityKg} kg</td>
                        <td data-label="Müşteri">{order.customerName}</td>
                        <td data-label="İşlem">
                          <button className="secondary" onClick={() => handleRevertShipment(order)}>Geri Al</button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {shippedTotalPages > 1 && (
                <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
                  <button className="secondary" disabled={shippedPage === 0} onClick={() => setShippedPage(shippedPage - 1)}>← Önceki</button>
                  <span>Sayfa {shippedPage + 1} / {shippedTotalPages}</span>
                  <button className="secondary" disabled={shippedPage >= shippedTotalPages - 1} onClick={() => setShippedPage(shippedPage + 1)}>Sonraki →</button>
                </div>
              )}
            </>
          )
        )}
      </div>
    </div>
  )
}

export default InventoryPage