import { useState, useEffect } from 'react'
import { authFetch } from './api'

function ManagementPanel() {
  const [isOpen, setIsOpen] = useState(false)
  const [materials, setMaterials] = useState([])
  const [suppliers, setSuppliers] = useState([])

  // yeni malzeme formu
  const [materialName, setMaterialName] = useState('')
  const [materialType, setMaterialType] = useState('RAW_MATERIAL')
  const [materialUnit, setMaterialUnit] = useState('kg')

  // yeni tedarikçi formu
  const [supplierName, setSupplierName] = useState('')
  const [supplierEmail, setSupplierEmail] = useState('')

  // bağlantı formu
  const [linkSupplierId, setLinkSupplierId] = useState('')
  const [linkMaterialId, setLinkMaterialId] = useState('')

  const [message, setMessage] = useState('')

  const [materialDescription, setMaterialDescription] = useState('')

  useEffect(() => {
    if (isOpen) {
      fetchMaterials()
      fetchSuppliers()
    }
  }, [isOpen])

  function fetchMaterials() {
    authFetch('http://localhost:8080/api/materials')
      .then(response => response.json())
      .then(data => setMaterials(data))
  }

  function fetchSuppliers() {
    authFetch('http://localhost:8080/api/suppliers')
      .then(response => response.json())
      .then(data => setSuppliers(data))
  }

  function handleAddMaterial() {
    authFetch('http://localhost:8080/api/materials', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: materialName, type: materialType, unit: materialUnit , description: materialDescription})

    })
      .then(response => response.json())
      .then(() => {
        setMaterialName('')
        setMessage('Malzeme eklendi: ' + materialName)
        fetchMaterials()

        setMaterialDescription('')

      })
  }

  function handleAddSupplier() {
    authFetch('http://localhost:8080/api/suppliers', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: supplierName, contactEmail: supplierEmail })
    })
      .then(response => response.json())
      .then(() => {
        setSupplierName('')
        setSupplierEmail('')
        setMessage('Tedarikçi eklendi: ' + supplierName)
        fetchSuppliers()
      })
  }

  function handleLink() {
    authFetch('http://localhost:8080/api/supplier-materials', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        supplier: { id: linkSupplierId },
        material: { id: linkMaterialId }
      })
    })
      .then(response => response.json())
      .then(() => {
        setMessage('Bağlantı oluşturuldu')
        setLinkSupplierId('')
        setLinkMaterialId('')
      })
  }

  return (
    <div className="card">
      <h2 onClick={() => setIsOpen(!isOpen)} style={{ cursor: 'pointer' }}>
        Malzeme & Tedarikçi Yönetimi {isOpen ? '▲' : '▼'}
      </h2>

      {isOpen && (
        <div>
          {message && (
            <div style={{ background: '#e8f5e9', color: '#1b5e20', padding: '0.6rem 1rem', borderRadius: '8px', marginBottom: '1rem' }}>
              ✅ {message}
            </div>
          )}

          <h3>Yeni Malzeme Ekle</h3>
          <input type="text" placeholder="İsim" value={materialName} onChange={(e) => setMaterialName(e.target.value)} />
          <select value={materialType} onChange={(e) => setMaterialType(e.target.value)}>
            <option value="RAW_MATERIAL">Hammadde</option>
            <option value="PACKAGING">Ambalaj</option>
          </select>
          <input type="text" placeholder="Birim (kg, adet)" value={materialUnit} onChange={(e) => setMaterialUnit(e.target.value)} />
          <input type="text" placeholder="Açıklama" value={materialDescription} onChange={(e) => setMaterialDescription(e.target.value)} />
           <button onClick={handleAddMaterial}>Ekle</button>

          <h3>Yeni Tedarikçi Ekle</h3>
          <input type="text" placeholder="İsim" value={supplierName} onChange={(e) => setSupplierName(e.target.value)} />
          <input type="text" placeholder="Email" value={supplierEmail} onChange={(e) => setSupplierEmail(e.target.value)} />
          <button onClick={handleAddSupplier}>Ekle</button>

          <h3>Tedarikçi-Malzeme Bağlantısı</h3>
          <select value={linkSupplierId} onChange={(e) => setLinkSupplierId(e.target.value)}>
            <option value="">Tedarikçi seçin</option>
            {suppliers.map(s => (
              <option key={s.id} value={s.id}>{s.name}</option>
            ))}
          </select>
          <select value={linkMaterialId} onChange={(e) => setLinkMaterialId(e.target.value)}>
            <option value="">Malzeme seçin</option>
            {materials.map(m => (
              <option key={m.id} value={m.id}>{m.name}</option>
            ))}
          </select>
          <button onClick={handleLink}>Bağla</button>
        </div>
      )}
    </div>
  )
}

export default ManagementPanel
