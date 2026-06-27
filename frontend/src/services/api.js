import axios from 'axios'

const USER_API = 'http://localhost:8081'
const MOVIE_API = 'http://localhost:8082'
const ANALYTICS_API = 'http://localhost:8083'

// Attach JWT to requests
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export const authAPI = {
  register: (data) => axios.post(`${USER_API}/api/auth/register`, data),
  login: (data) => axios.post(`${USER_API}/api/auth/login`, data),
  validate: () => axios.get(`${USER_API}/api/auth/validate`)
}

export const movieAPI = {
  getAll: () => axios.get(`${MOVIE_API}/api/movies`),
  get: (id) => axios.get(`${MOVIE_API}/api/movies/${id}`),
  byGenre: (g) => axios.get(`${MOVIE_API}/api/movies/genre/${g}`),
  search: (q) => axios.get(`${MOVIE_API}/api/movies/search?q=${encodeURIComponent(q)}`),
  trending: (limit = 10) => axios.get(`${MOVIE_API}/api/movies/trending?limit=${limit}`),
  genres: () => axios.get(`${MOVIE_API}/api/movies/genres`),
  recordView: (id, userId, duration) =>
    axios.post(`${MOVIE_API}/api/movies/${id}/view`, { userId, duration })
}

export const analyticsAPI = {
  dashboard: () => axios.get(`${ANALYTICS_API}/api/analytics/dashboard`)
}
