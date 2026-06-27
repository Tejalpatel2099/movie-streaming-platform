package com.streaming.movie.repository;

import com.streaming.movie.entity.ViewEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ViewEventRepository extends JpaRepository<ViewEvent, Long> {

    long countByViewedAtAfter(LocalDateTime since);

    @Query("SELECT COUNT(DISTINCT v.userId) FROM ViewEvent v WHERE v.viewedAt >= :since")
    long countDistinctUsersSince(@Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(v.watchDurationSeconds), 0) FROM ViewEvent v WHERE v.viewedAt >= :since")
    long totalWatchSecondsSince(@Param("since") LocalDateTime since);

    @Query(value = "SELECT FORMATDATETIME(viewed_at, 'yyyy-MM-dd HH:00') AS hr, COUNT(*) AS cnt " +
                   "FROM view_events WHERE viewed_at >= :since " +
                   "GROUP BY hr ORDER BY hr", nativeQuery = true)
    List<Object[]> viewsByHour(@Param("since") LocalDateTime since);

    @Query("SELECT v.movieId, COUNT(v) as c FROM ViewEvent v " +
           "WHERE v.viewedAt >= :since GROUP BY v.movieId ORDER BY c DESC")
    List<Object[]> topMovies(@Param("since") LocalDateTime since);
}
