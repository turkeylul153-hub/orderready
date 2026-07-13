import { useState, useEffect } from 'react'

function CalculatorPage() {
  // ürün seçim listesi için tüm ürünleri tutar
  const [products, setProducts] = useState([])

  // kullanıcının seçtiği ürün id'si ve girdiği miktar
  const [selectedProductId, setSelectedProductId] = useState('')
  const [quantity, setQuantity] = useState('')

  // backendden dönen hesaplama sonucunu tutar
  const [result, setResult] = useState(null)

  // sayfa açılınca ürün listesini çek (dropdown'ı doldurmak için)
  useEffect(() => {
    fetch('http://localhost:8080/api/products')
      .then(response => response.json())
      .then(data => setProducts(data))
  }, [])

  // "hesapla" butonuna basılınca çalışır
  function handleCalculate() {
    fetch('http://localhost:8080/api/calculate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        productId: selectedProductId,
        quantityKg: quantity
      })
    })
      .then(response => response.json())
      .then(data => setResult(data))
  }

  return (
    <div>
      <h2>Üretim planlama hesaplama</h2>

      {/* ürün seçim kutusu */}
      <select value={selectedProductId} onChange={(e) => setSelectedProductId(e.target.value)}>
        <option value="">Ürün seçin</option>
        {products.map(product => (
          <option key={product.id} value={product.id}>{product.name}</option>
        ))}
      </select>

      {/* miktar girişi */}
      <input
        type="number"
        placeholder="Miktar (kg)"
        value={quantity}
        onChange={(e) => setQuantity(e.target.value)}
      />

      <button onClick={handleCalculate}>Hesapla</button>

      {/* sonuç varsa göster */}
      {result && (
        <div>
          <h3>{result.productName} - {result.requestedQuantity} kg</h3>
          <p>Üretim süresi: {result.productionTimeHours} saat</p>
          <p>Tedarik süresi: {result.supplyTimeDays} gün</p>
          <p><strong>Toplam süre: {result.totalTimeHours} saat</strong></p>

          <table border="1" cellPadding="8">
            <thead>
              <tr>
                <th>Malzeme</th>
                <th>Gereken</th>
                <th>Fabrikada</th>
                <th>Eksik</th>
                <th>Tedarikçi</th>
                <th>Tedarikçide hazır mı</th>
                <th>Tahmini süre (gün)</th>
              </tr>
            </thead>
            <tbody>
              {result.materialStatuses.map((status, index) => (
                <tr key={index}>
                  <td>{status.materialName}</td>
                  <td>{status.requiredQuantity} {status.unit}</td>
                  <td>{status.factoryStock} {status.unit}</td>
                  <td>{status.shortfall} {status.unit}</td>
                  <td>{status.supplierName ?? '-'}</td>
                  <td>{status.supplierHasStock ? 'Evet' : 'Hayır'}</td>
                  <td>{status.estimatedSupplyDays ?? '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

export default CalculatorPage
