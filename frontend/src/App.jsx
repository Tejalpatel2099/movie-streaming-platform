import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import MovieDetail from './pages/MovieDetail'
import Dashboard from './pages/Dashboard'

const Protected = ({ children, adminOnly }) => {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" />
  if (adminOnly && user.role !== 'ADMIN') return <Navigate to="/" />
  return children
}

export default function App() {
  return (
    <AuthProvider>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/movie/:id" element={<Protected><MovieDetail /></Protected>} />
        <Route path="/admin" element={<Protected adminOnly><Dashboard /></Protected>} />
      </Routes>
    </AuthProvider>
  )
}
