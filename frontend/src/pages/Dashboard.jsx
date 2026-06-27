import { useEffect, useState } from 'react'
import {
  LineChart, Line, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, Area, AreaChart
} from 'recharts'
import { analyticsAPI, movieAPI } from '../services/api'

const COLORS = ['#e50914', '#0066ff', '#10b981', '#f59e0b', '#8b5cf6', '#ec4899']

export default function Dashboard() {
  const [data, setData] = useState(null)
  const [topMovieDetails, setTopMovieDetails] = useState([])
  const [genreData, setGenreData] = useState([])
  const [lastUpdate, setLastUpdate] = useState(new Date())

  const loadData = async () => {
    try {
      const dashRes = await analyticsAPI.dashboard()
      setData(dashRes.data)
      setLastUpdate(new Date())

      // Resolve top movie names
      const topMovies = dashRes.data.topMovies || []
      const details = await Promise.all(
        topMovies.map(async ([movieId, count]) => {
          try {
            const m = await movieAPI.get(movieId)
            return { name: m.data.title, views: count }
          } catch { return { name: `Movie ${movieId}`, views: count } }
        })
      )
      setTopMovieDetails(details)

      // Genre breakdown
      const allMovies = await movieAPI.getAll()
      const byGenre = {}
      allMovies.data.forEach(m => {
        byGenre[m.genre] = (byGenre[m.genre] || 0) + (m.viewCount || 0)
      })
      setGenreData(Object.entries(byGenre).map(([name, value]) => ({ name, value })))
    } catch (e) {
      console.error(e)
    }
  }

  useEffect(() => {
    loadData()
    const interval = setInterval(loadData, 10000) // refresh every 10s
    return () => clearInterval(interval)
  }, [])

  if (!data) return <div className="loading">Loading dashboard...</div>

  // Transform viewsByHour data
  const hourlyViews = (data.viewsByHour || []).map(([hr, cnt]) => ({
    hour: hr.split(' ')[1] || hr,
    views: cnt
  }))

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>📊 Admin Analytics</h1>
        <span className="last-update">Auto-refresh • Last: {lastUpdate.toLocaleTimeString()}</span>
      </div>

      {/* KPI Cards - 10 KPIs */}
      <div className="kpi-grid">
        <KpiCard label="Total Users" value={data.totalUsers} icon="👥" color="#0066ff" />
        <KpiCard label="Active (24h)" value={data.activeUsers24h} icon="🟢" color="#10b981" />
        <KpiCard label="New Users (7d)" value={data.newUsers7d} icon="✨" color="#8b5cf6" />
        <KpiCard label="Engagement Rate" value={`${data.engagementRate}%`} icon="📈" color="#f59e0b" />
        <KpiCard label="Total Movies" value={data.totalMovies} icon="🎬" color="#e50914" />
        <KpiCard label="Total Views" value={data.totalViews} icon="👁️" color="#06b6d4" />
        <KpiCard label="Views (24h)" value={data.views24h} icon="📺" color="#0066ff" />
        <KpiCard label="Unique Viewers (24h)" value={data.uniqueViewers24h} icon="🎯" color="#10b981" />
        <KpiCard label="Watch Hours" value={Math.round(data.totalWatchHours)} icon="⏱️" color="#8b5cf6" />
        <KpiCard label="Avg Views/User" value={data.avgViewsPerUser} icon="📊" color="#ec4899" />
      </div>

      {/* Charts */}
      <div className="charts-row">
        <div className="chart-card large">
          <h3>Views Over Time (Last 24h)</h3>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={hourlyViews}>
              <defs>
                <linearGradient id="colorViews" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#e50914" stopOpacity={0.8}/>
                  <stop offset="95%" stopColor="#e50914" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#333" />
              <XAxis dataKey="hour" stroke="#888" />
              <YAxis stroke="#888" />
              <Tooltip contentStyle={{ background: '#1a1a1a', border: '1px solid #333' }} />
              <Area type="monotone" dataKey="views" stroke="#e50914" fillOpacity={1} fill="url(#colorViews)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="charts-row">
        <div className="chart-card">
          <h3>Top Movies (Last 7 Days)</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={topMovieDetails} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" stroke="#333" />
              <XAxis type="number" stroke="#888" />
              <YAxis dataKey="name" type="category" width={130} stroke="#888" />
              <Tooltip contentStyle={{ background: '#1a1a1a', border: '1px solid #333' }} />
              <Bar dataKey="views" fill="#e50914" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-card">
          <h3>Views by Genre</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={genreData}
                cx="50%" cy="50%"
                outerRadius={100}
                dataKey="value"
                label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
              >
                {genreData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
              </Pie>
              <Tooltip contentStyle={{ background: '#1a1a1a', border: '1px solid #333' }} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  )
}

function KpiCard({ label, value, icon, color }) {
  return (
    <div className="kpi-card">
      <div className="kpi-icon" style={{ background: color + '22', color }}>{icon}</div>
      <div className="kpi-info">
        <span className="kpi-label">{label}</span>
        <span className="kpi-value">{value}</span>
      </div>
    </div>
  )
}
