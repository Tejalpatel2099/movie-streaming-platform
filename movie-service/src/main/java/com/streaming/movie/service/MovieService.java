package com.streaming.movie.service;

import com.streaming.movie.entity.Movie;
import com.streaming.movie.entity.ViewEvent;
import com.streaming.movie.repository.MovieRepository;
import com.streaming.movie.repository.ViewEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final ViewEventRepository viewEventRepository;

    @Cacheable("movies")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Cacheable(value = "movie", key = "#id")
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    @Cacheable(value = "moviesByGenre", key = "#genre")
    public List<Movie> getByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    public List<Movie> search(String q) {
        return movieRepository.searchByTitle(q);
    }

    public List<Movie> getTrending(int limit) {
        return movieRepository.findTopByViewCount(PageRequest.of(0, limit));
    }

    public List<String> getGenres() {
        return movieRepository.findAllGenres();
    }

    @Transactional
    @CacheEvict(value = {"movies", "movie", "moviesByGenre"}, allEntries = true)
    public ViewEvent recordView(Long movieId, Long userId, Integer durationSeconds) {
        movieRepository.incrementViewCount(movieId);
        ViewEvent event = ViewEvent.builder()
                .movieId(movieId)
                .userId(userId)
                .watchDurationSeconds(durationSeconds == null ? 0 : durationSeconds)
                .build();
        return viewEventRepository.save(event);
    }
}
