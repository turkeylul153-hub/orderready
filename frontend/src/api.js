export function authFetch(url, options = {}) {
  const token = localStorage.getItem('token')
  const headers = {
    ...(options.headers || {}),
    ...(token ? { Authorization: token } : {})
  }
  return fetch(url, { ...options, headers })
}