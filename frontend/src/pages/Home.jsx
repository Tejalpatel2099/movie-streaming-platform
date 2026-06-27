import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { movieAPI } from '../services/api'

export default function Home() {
  const [movies, setMovies] = useState([])
  const [trending, setTrending] = useState([])
  const [featured, setFeatured] = useState(null)
  const [genres, setGenres] = useState([])
  const [selectedGenre, setSelectedGenre] = useState('All')
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      movieAPI.getAll(),
      movieAPI.trending(6),
      movieAPI.genres()
    ]).then(([all, trend, gen]) => {
      setMovies(all.data)
      setTrending(trend.data)
      setGenres(gen.data)
      // Use the highest-rated movie as the featured hero
      const top = [...all.data].sort((a, b) => b.rating - a.rating)[0]
      setFeatured(top)
      setLoading(false)
    }).catch(() => setLoading(false))
  }, [])

  const filterByGenre = async (g) => {
    setSelectedGenre(g)
    if (g === 'All') {
      const res = await movieAPI.getAll()
      setMovies(res.data)
    } else {
      const res = await movieAPI.byGenre(g)
      setMovies(res.data)
    }
  }

  const handleSearch = async (e) => {
    e.preventDefault()
    if (!search.trim()) {
      const res = await movieAPI.getAll()
      setMovies(res.data)
    } else {
      const res = await movieAPI.search(search)
      setMovies(res.data)
    }
  }

  if (loading) return <div className="loading">Loading...</div>

  return (
    <div className="home">
      {/* Netflix-style hero banner with featured movie */}
      {featured && (
        <section
          className="hero-banner"
          style={{ backgroundImage: `linear-gradient(to right, rgba(0,0,0,0.95) 0%, rgba(0,0,0,0.6) 50%, rgba(0,0,0,0.3) 100%), url(${featured.thumbnailUrl})` }}
        >
          <div className="hero-content">
            <span className="hero-badge">⭐ Featured</span>
            <h1 className="hero-title">{featured.title}</h1>
            <div className="hero-meta">
              <span className="rating-badge">★ {featured.rating}</span>
              <span>{featured.releaseYear}</span>
              <span>{featured.durationMinutes} min</span>
              <span className="genre-tag">{featured.genre}</span>
            </div>
            <p className="hero-description">{featured.description}</p>
            <div className="hero-actions">
              <Link to={`/movie/${featured.id}`} className="btn-play">▶ Play</Link>
              <Link to={`/movie/${featured.id}`} className="btn-info">ⓘ More Info</Link>
            </div>
          </div>
        </section>
      )}

      <section className="search-section">
        <form onSubmit={handleSearch} className="search-bar">
          <input
            type="text"
            placeholder="Search movies..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <button type="submit">Search</button>
        </form>
      </section>

      {trending.length > 0 && (
        <section className="section">
          <h2>🔥 Trending Now</h2>
          <div className="movie-row">
            {trending.map(m => <MovieCard key={m.id} movie={m} />)}
          </div>
        </section>
      )}

      <section className="section">
        <div className="section-header">
          <h2>Browse</h2>
          <div className="genre-filter">
            <button
              className={selectedGenre === 'All' ? 'active' : ''}
              onClick={() => filterByGenre('All')}
            >All</button>
            {genres.map(g => (
              <button
                key={g}
                className={selectedGenre === g ? 'active' : ''}
                onClick={() => filterByGenre(g)}
              >{g}</button>
            ))}
          </div>
        </div>
        <div className="movie-grid">
          {movies.map(m => <MovieCard key={m.id} movie={m} />)}
        </div>
      </section>
    </div>
  )
}

// Styled poster placeholder used if a remote image ever fails to load,
// so a card never falls back to a broken-image icon.
function posterFallback(title) {
  const svg = `<svg xmlns='http://www.w3.org/2000/svg' width='400' height='600'>
    <defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'>
      <stop offset='0' stop-color='#1f1f29'/><stop offset='1' stop-color='#0a0a0a'/>
    </linearGradient></defs>
    <rect width='400' height='600' fill='url(#g)'/>
    <text x='50%' y='48%' fill='#e50914' font-size='64' font-family='sans-serif'
      font-weight='bold' text-anchor='middle'>★</text>
    <text x='50%' y='58%' fill='#cccccc' font-size='22' font-family='sans-serif'
      text-anchor='middle'>${title.replace(/[<&>]/g, '')}</text>
  </svg>`
  return `data:image/svg+xml,${encodeURIComponent(svg)}`
}

function MovieCard({ movie }) {
  return (
    <Link to={`/movie/${movie.id}`} className="movie-card">
      <img
        src={movie.thumbnailUrl}
        alt={movie.title}
        loading="lazy"
        onError={(e) => {
          e.target.onerror = null
          e.target.src = posterFallback(movie.title)
        }}
      />
      <div className="movie-info">
        <h3>{movie.title}</h3>
        <div className="meta">
          <span>{movie.releaseYear}</span>
          <span>★ {movie.rating}</span>
        </div>
        <span className="genre-tag">{movie.genre}</span>
      </div>
    </Link>
  )
}