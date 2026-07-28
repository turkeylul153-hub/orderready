import { useState, useEffect } from 'react'
import { authFetch } from './api'
import Toast from './Toast'

const allRoles = ['WAREHOUSE', 'SALES', 'PLANNER', 'ADMIN']

function UserManagementPage() {
  const [users, setUsers] = useState([])
  const [toast, setToast] = useState(null)

  useEffect(() => {
    fetchUsers()
  }, [])

  function fetchUsers() {
    authFetch('http://localhost:8080/api/users')
      .then(response => response.json())
      .then(data => setUsers(data))
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

  return (
    <div className="card">
      <Toast toast={toast} onClose={() => setToast(null)} />

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
                <td data-label="İşlem" style={{ flexDirection: 'row', flexWrap: 'wrap', gap: '0.4rem' }}>
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
  )
}

export default UserManagementPage