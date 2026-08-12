import { useState, useEffect } from 'react'
import { authFetch } from './api'
import Toast from './Toast'

const allRoles = ['WAREHOUSE', 'SALES', 'PLANNER', 'ADMIN']

function UserManagementPage() {
  const [users, setUsers] = useState([])
  const [requests, setRequests] = useState([])
  const [toast, setToast] = useState(null)

  useEffect(() => {
    fetchUsers()
    fetchRequests()
  }, [])

  function fetchUsers() {
    authFetch('http://localhost:8080/api/users')
      .then(response => response.json())
      .then(data => setUsers(data))
  }

  function fetchRequests() {
    authFetch('http://localhost:8080/api/access-requests')
      .then(response => response.json())
      .then(data => setRequests(data))
  }

  function hasRole(user, role) {
    return user.role.split(',').includes(role)
  }

  function toggleRole(user, role) {
    const currentRoles = user.role.split(',')
    let newRoles

    if (currentRoles.includes(role)) {
      newRoles = currentRoles.filter(r => r !== role)
    } else {
      newRoles = [...currentRoles, role]
    }

    if (newRoles.length === 0) {
      setToast({ message: 'Bir kullanıcının en az bir rolü olmalı', type: 'error' })
      return
    }

    authFetch(`http://localhost:8080/api/users/${user.id}/role`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ role: newRoles.join(',') })
    })
      .then(response => response.json())
      .then(() => {
        setToast({ message: `${user.username} için roller güncellendi`, type: 'success' })
        fetchUsers()
      })
      .catch(() => setToast({ message: 'Güncelleme başarısız oldu', type: 'error' }))
  }

  function handleApprove(requestId) {
    authFetch(`http://localhost:8080/api/access-requests/${requestId}/approve`, {
      method: 'PUT'
    })
      .then(response => response.json())
      .then(() => {
        setToast({ message: 'Talep onaylandı', type: 'success' })
        fetchRequests()
        fetchUsers()
      })
      .catch(() => setToast({ message: 'İşlem başarısız oldu', type: 'error' }))
  }

  function handleReject(requestId) {
    authFetch(`http://localhost:8080/api/access-requests/${requestId}/reject`, {
      method: 'PUT'
    })
      .then(response => response.json())
      .then(() => {
        setToast({ message: 'Talep reddedildi', type: 'success' })
        fetchRequests()
      })
      .catch(() => setToast({ message: 'İşlem başarısız oldu', type: 'error' }))
  }

  return (
    <div>
      <Toast toast={toast} onClose={() => setToast(null)} />

      <div className="card">
        <h2>Bekleyen Yetki Talepleri</h2>
        {requests.length === 0 ? (
          <div className="empty-state">📋 Şu an bekleyen talep yok.</div>
        ) : (
          <div className="table-scroll">
            <table className="responsive-table">
              <thead>
                <tr>
                  <th>Kullanıcı</th>
                  <th>Talep Edilen Rol</th>
                  <th>Tarih</th>
                  <th>İşlem</th>
                </tr>
              </thead>
              <tbody>
                {requests.map(request => (
                  <tr key={request.id}>
                    <td data-label="Kullanıcı">{request.user.username}</td>
                    <td data-label="Talep Edilen Rol">{request.requestedRole}</td>
                    <td data-label="Tarih">{request.createdAt}</td>
                    <td data-label="İşlem" className="action-cell">
                      <button onClick={() => handleApprove(request.id)}>Onayla</button>
                      <button className="danger" onClick={() => handleReject(request.id)}>Reddet</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <div className="card">
        <h2>Kullanıcı Yönetimi</h2>

        <div className="table-scroll">
          <table className="responsive-table">
            <thead>
              <tr>
                <th>Kullanıcı Adı</th>
                <th>Roller</th>
                <th>İşlem</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id}>
                  <td data-label="Kullanıcı Adı">{user.username}</td>
                  <td data-label="Roller">{user.role}</td>
                  <td data-label="İşlem" className="action-cell">
                    {allRoles.map(role => (
                      <button
                        key={role}
                        className={hasRole(user, role) ? '' : 'secondary'}
                        onClick={() => toggleRole(user, role)}
                      >
                        {hasRole(user, role) ? `${role} ✓` : `+ ${role}`}
                      </button>
                    ))}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default UserManagementPage