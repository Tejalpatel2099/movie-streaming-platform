package com.streaming.movie.config;

import com.streaming.movie.entity.Movie;
import com.streaming.movie.entity.ViewEvent;
import com.streaming.movie.repository.MovieRepository;
import com.streaming.movie.repository.ViewEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final ViewEventRepository viewEventRepository;

    // Generates a poster-like image with the movie title typeset on it
    private String poster(String title, String bgColor) {
        String encoded = URLEncoder.encode(title, StandardCharsets.UTF_8);
        return "https://placehold.co/400x600/" + bgColor + "/ffffff?text=" + encoded + "&font=playfair-display";
    }

    @Override
    public void run(String... args) {
        if (movieRepository.count() > 0)
            return;

        // Reliable free demo video URLs that work in all browsers
        String v1 = "https://www.w3schools.com/html/mov_bbb.mp4";
        String v2 = "https://download.samplelib.com/mp4/sample-30s.mp4";
        String v3 = "https://download.samplelib.com/mp4/sample-20s.mp4";
        String v4 = "https://download.samplelib.com/mp4/sample-15s.mp4";
        String v5 = "https://download.samplelib.com/mp4/sample-10s.mp4";
        String v6 = "https://download.samplelib.com/mp4/sample-5s.mp4";

        List<Movie> seed = List.of(
                Movie.builder().title("Casablanca")
                        .description(
                                "In WWII Casablanca, a cynical American expatriate meets a former lover, with unforeseen complications.")
                        .genre("Drama").releaseYear(1942).durationMinutes(102)
                        .thumbnailUrl(poster("Casablanca", "1a1a2e"))
                        .streamUrl(v1).rating(8.5).viewCount(0L).build(),

                Movie.builder().title("Metropolis")
                        .description(
                                "A futuristic city sharply divided between the working class and the city planners; the son of the city's mastermind falls in love with a working-class prophet.")
                        .genre("Sci-Fi").releaseYear(1927).durationMinutes(153)
                        .thumbnailUrl(poster("METROPOLIS", "16213e"))
                        .streamUrl(v2).rating(8.3).viewCount(0L).build(),

                Movie.builder().title("Nosferatu")
                        .description(
                                "Vampire Count Orlok expresses interest in a new residence and his real estate agent's wife.")
                        .genre("Horror").releaseYear(1922).durationMinutes(94)
                        .thumbnailUrl(poster("Nosferatu", "2c0e0e"))
                        .streamUrl(v3).rating(7.9).viewCount(0L).build(),

                Movie.builder().title("The General")
                        .description(
                                "During the American Civil War, a train engineer pursues Union soldiers who stole his beloved locomotive and his fiancée.")
                        .genre("Comedy").releaseYear(1926).durationMinutes(78)
                        .thumbnailUrl(poster("The General", "3d2914"))
                        .streamUrl(v4).rating(8.1).viewCount(0L).build(),

                Movie.builder().title("Night of the Living Dead")
                        .description(
                                "A ragtag group of Pennsylvanians barricade themselves in an old farmhouse to remain safe from a horde of flesh-eating ghouls.")
                        .genre("Horror").releaseYear(1968).durationMinutes(96)
                        .thumbnailUrl(poster("Night of the Living Dead", "0d0d0d"))
                        .streamUrl(v5).rating(7.8).viewCount(0L).build(),

                Movie.builder().title("Charade")
                        .description(
                                "Romance and suspense ensue in Paris as a woman is pursued by several men who want a fortune her murdered husband had stolen.")
                        .genre("Thriller").releaseYear(1963).durationMinutes(113)
                        .thumbnailUrl(poster("Charade", "8b0000"))
                        .streamUrl(v6).rating(7.9).viewCount(0L).build(),

                Movie.builder().title("His Girl Friday")
                        .description(
                                "A newspaper editor uses every trick in the book to keep his ace reporter ex-wife from remarrying.")
                        .genre("Comedy").releaseYear(1940).durationMinutes(92)
                        .thumbnailUrl(poster("His Girl Friday", "2d4a2b"))
                        .streamUrl(v1).rating(7.9).viewCount(0L).build(),

                Movie.builder().title("D.O.A.")
                        .description(
                                "Frank Bigelow, told he's been poisoned and has only a few days to live, tries to find out who killed him and why.")
                        .genre("Thriller").releaseYear(1949).durationMinutes(83)
                        .thumbnailUrl(poster("D.O.A.", "1a1a1a"))
                        .streamUrl(v2).rating(7.4).viewCount(0L).build(),

                Movie.builder().title("Detour")
                        .description(
                                "A hitchhiker's life takes a series of dark turns when he attempts to journey from New York to Hollywood.")
                        .genre("Thriller").releaseYear(1945).durationMinutes(68)
                        .thumbnailUrl(poster("Detour", "2a1a3a"))
                        .streamUrl(v3).rating(7.3).viewCount(0L).build(),

                Movie.builder().title("Plan 9 from Outer Space")
                        .description(
                                "Aliens resurrect dead humans as zombies and vampires to stop humanity from creating a doomsday weapon.")
                        .genre("Sci-Fi").releaseYear(1959).durationMinutes(79)
                        .thumbnailUrl(poster("Plan 9 from Outer Space", "0a2a3a"))
                        .streamUrl(v4).rating(4.0).viewCount(0L).build(),

                Movie.builder().title("Carnival of Souls")
                        .description(
                                "After a traumatic accident, a woman becomes drawn to a mysterious abandoned carnival.")
                        .genre("Horror").releaseYear(1962).durationMinutes(78)
                        .thumbnailUrl(poster("Carnival of Souls", "1a0a1a"))
                        .streamUrl(v5).rating(7.0).viewCount(0L).build(),

                Movie.builder().title("The Phantom of the Opera")
                        .description("A mad, disfigured composer seeks love with a lovely young opera singer in Paris.")
                        .genre("Horror").releaseYear(1925).durationMinutes(106)
                        .thumbnailUrl(poster("Phantom of the Opera", "3a0a0a"))
                        .streamUrl(v6).rating(7.5).viewCount(0L).build());
        movieRepository.saveAll(seed);

        // Seed 800 view events spread realistically across the last 24 hours
        Random rand = new Random(42);
        List<Movie> saved = movieRepository.findAll();
        for (int i = 0; i < 800; i++) {
            Movie m = saved.get(rand.nextInt(saved.size()));
            int hoursAgo = rand.nextInt(24);
            int minutesAgo = rand.nextInt(60);
            LocalDateTime when = LocalDateTime.now().minusHours(hoursAgo).minusMinutes(minutesAgo);
            ViewEvent ev = ViewEvent.builder()
                    .movieId(m.getId())
                    .userId((long) (rand.nextInt(150) + 1))
                    .watchDurationSeconds(rand.nextInt(7200))
                    .viewedAt(when)
                    .build();
            viewEventRepository.save(ev);
            m.setViewCount(m.getViewCount() + 1);
        }
        movieRepository.saveAll(saved);

        System.out.println(">>> Seeded " + saved.size() + " classic films and 800 view events");
    }
}