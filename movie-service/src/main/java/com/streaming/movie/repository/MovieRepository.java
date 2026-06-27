package com.streaming.movie.repository;

import com.streaming.movie.entity.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByGenre(String genre);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Movie> searchByTitle(@Param("q") String q);

    @Query("SELECT m FROM Movie m ORDER BY m.viewCount DESC")
    List<Movie> findTopByViewCount(Pageable pageable);

    @Query("SELECT DISTINCT m.genre FROM Movie m WHERE m.genre IS NOT NULL")
    List<String> findAllGenres();

    @Modifying
    @Query("UPDATE Movie m SET m.viewCount = m.viewCount + 1 WHERE m.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
