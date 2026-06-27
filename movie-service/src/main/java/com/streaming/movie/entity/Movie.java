package com.streaming.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movies", indexes = {
    @Index(name = "idx_genre", columnList = "genre"),
    @Index(name = "idx_title", columnList = "title")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(length = 50)
    private String genre;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "stream_url", length = 500)
    private String streamUrl;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "rating")
    private Double rating = 0.0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
