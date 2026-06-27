package com.streaming.movie.controller;

import com.streaming.movie.entity.Movie;
import com.streaming.movie.repository.MovieRepository;
import com.streaming.movie.repository.ViewEventRepository;
import com.streaming.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;
    private final MovieRepository movieRepository;
    private final ViewEventRepository viewEventRepository;

    @GetMapping
    public List<Movie> getAll() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> get(@PathVariable Long id) {
        return movieService.getMovieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/genre/{genre}")
    public List<Movie> byGenre(@PathVariable String genre) {
        return movieService.getByGenre(genre);
    }

    @GetMapping("/search")
    public List<Movie> search(@RequestParam String q) {
        return movieService.search(q);
    }

    @GetMapping("/trending")
    public List<Movie> trending(@RequestParam(defaultValue = "10") int limit) {
        return movieService.getTrending(limit);
    }

    @GetMapping("/genres")
    public List<String> genres() {
        return movieService.getGenres();
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<?> view(@PathVariable Long id,
                                  @RequestBody(required = false) Map<String, Object> body) {
        Long userId = body != null && body.get("userId") != null
                ? Long.valueOf(body.get("userId").toString()) : null;
        Integer duration = body != null && body.get("duration") != null
                ? Integer.valueOf(body.get("duration").toString()) : 0;
        return ResponseEntity.ok(movieService.recordView(id, userId, duration));
    }

    // Internal endpoint for analytics service
    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);

        Map<String, Object> result = new HashMap<>();
        result.put("totalMovies", movieRepository.count());
        result.put("totalViews", viewEventRepository.count());
        result.put("views24h", viewEventRepository.countByViewedAtAfter(dayAgo));
        result.put("uniqueViewers24h", viewEventRepository.countDistinctUsersSince(dayAgo));
        result.put("totalWatchHours", viewEventRepository.totalWatchSecondsSince(weekAgo) / 3600.0);
        result.put("viewsByHour", viewEventRepository.viewsByHour(dayAgo));
        result.put("topMovies", viewEventRepository.topMovies(weekAgo).stream().limit(5).toList());
        return ResponseEntity.ok(result);
    }
}
