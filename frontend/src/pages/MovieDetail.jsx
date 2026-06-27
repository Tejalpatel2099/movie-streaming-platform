import { useEffect, useRef, useState } from 'react'
import { useParams } from 'react-router-dom'
import { movieAPI } from '../services/api'
import { useAuth } from '../context/AuthContext'

export default function MovieDetail() {
  const { id } = useParams()
  const { user } = useAuth()
  const [movie, setMovie] = useState(null)
  const [playing, setPlaying] = useState(false)
  const videoRef = useRef(null)
  const startTimeRef = useRef(null)

  useEffect(() => {
    movieAPI.get(id).then(res => setMovie(res.data))
  }, [id])

  const handlePlay = () => {
    setPlaying(true)
    startTimeRef.current = Date.now()
  }

  const handleEnded = () => {
    const duration = Math.floor((Date.now() - startTimeRef.current) / 1000)
    movieAPI.recordView(id, user.userId, duration).catch(() => {})
  }

  const handlePause = () => {
    if (startTimeRef.current) {
      const duration = Math.floor((Date.now() - startTimeRef.current) / 1000)
      if (duration > 5) {  // only record if watched > 5s
        movieAPI.recordView(id, user.userId, duration).catch(() => {})
      }
      startTimeRef.current = null
    }
  }

  if (!movie) return <div className="loading">Loading...</div>

  return (
    <div className="movie-detail">
      {playing ? (
        <video
          ref={videoRef}
          src={movie.streamUrl}
          controls
          autoPlay
          onPause={handlePause}
          onEnded={handleEnded}
          className="video-player"
        />
      ) : (
        <div className="player-poster" style={{ backgroundImage: `url(${movie.thumbnailUrl})` }}>
          <button className="play-btn" onClick={handlePlay}>▶ Play</button>
        </div>
      )}

      <div className="movie-meta">
        <h1>{movie.title}</h1>
        <div className="meta-row">
          <span>{movie.releaseYear}</span>
          <span>{movie.durationMinutes} min</span>
          <span>⭐ {movie.rating}</span>
          <span className="genre-tag">{movie.genre}</span>
        </div>
        <p className="description">{movie.description}</p>
        <p className="view-count">{movie.viewCount} views</p>
      </div>
    </div>
  )
}
