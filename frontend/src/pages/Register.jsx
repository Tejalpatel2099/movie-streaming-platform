import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authAPI } from '../services/api'
import { useAuth } from '../context/AuthContext'

export default function Register() {
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [error, setError] = useState('')
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const res = await authAPI.register(form)
      login(res.data)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.error || 'Registration failed')
    }
  }

  return (
    <div className="auth-page">
      <form onSubmit={handleSubmit} className="auth-form">
        <h2>Create your account</h2>
        {error && <div className="error">{error}</div>}
        <input
          type="text" placeholder="Username" minLength={3}
          value={form.username}
          onChange={(e) => setForm({ ...form, username: e.target.value })} required
        />
        <input
          type="email" placeholder="Email"
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })} required
        />
        <input
          type="password" placeholder="Password (min 6 chars)" minLength={6}
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })} required
        />
        <button type="submit">Sign Up</button>
        <p>Have an account? <Link to="/login">Login</Link></p>
      </form>
    </div>
  )
}
