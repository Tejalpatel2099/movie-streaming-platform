import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authAPI } from '../services/api'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const res = await authAPI.login({ email, password })
      login(res.data)
      navigate(res.data.role === 'ADMIN' ? '/admin' : '/')
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed')
    }
  }

  return (
    <div className="auth-page">
      <form onSubmit={handleSubmit} className="auth-form">
        <h2>Welcome back</h2>
        {error && <div className="error">{error}</div>}
        <input
          type="email" placeholder="Email"
          value={email} onChange={(e) => setEmail(e.target.value)} required
        />
        <input
          type="password" placeholder="Password"
          value={password} onChange={(e) => setPassword(e.target.value)} required
        />
        <button type="submit">Login</button>
        <p>No account? <Link to="/register">Sign up</Link></p>
        <div className="demo-hint">
          <strong>Demo accounts:</strong><br/>
          Admin: admin@stream.com / admin123<br/>
          User: user@stream.com / user123
        </div>
      </form>
    </div>
  )
}
