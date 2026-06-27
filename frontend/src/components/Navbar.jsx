import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  return (
    <nav className="navbar">
      <Link to="/" className="logo">🎬 StreamHub</Link>
      <div className="nav-links">
        {user ? (
          <>
            {user.role === 'ADMIN' && <Link to="/admin">Admin Dashboard</Link>}
            <span className="user-greeting">Hi, {user.username}</span>
            <button onClick={() => { logout(); navigate('/') }} className="btn-link">Logout</button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register" className="btn-primary">Sign Up</Link>
          </>
        )}
      </div>
    </nav>
  )
}
