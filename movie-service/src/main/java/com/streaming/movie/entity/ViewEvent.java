package com.streaming.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "view_events", indexes = {
    @Index(name = "idx_movie_id", columnList = "movie_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_viewed_at", columnList = "viewed_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ViewEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "watch_duration_seconds")
    private Integer watchDurationSeconds = 0;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    @PrePersist
    protected void onCreate() {
        viewedAt = LocalDateTime.now();
    }
}
