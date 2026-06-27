package com.streaming.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final WebClient webClient;

    @Value("${services.user.url:http://localhost:8081}")
    private String userServiceUrl;

    @Value("${services.movie.url:http://localhost:8082}")
    private String movieServiceUrl;

    @Cacheable("dashboard")
    @SuppressWarnings("unchecked")
    public Map<String, Object> getDashboardKpis() {
        Map<String, Object> userStats = fetchStats(userServiceUrl + "/api/auth/stats");
        Map<String, Object> movieStats = fetchStats(movieServiceUrl + "/api/movies/stats");

        Map<String, Object> dashboard = new HashMap<>();

        // 10+ KPIs as mentioned in the resume
        dashboard.put("totalUsers", userStats.getOrDefault("totalUsers", 0));
        dashboard.put("activeUsers24h", userStats.getOrDefault("activeUsers24h", 0));
        dashboard.put("newUsers7d", userStats.getOrDefault("newUsers7d", 0));
        dashboard.put("totalMovies", movieStats.getOrDefault("totalMovies", 0));
        dashboard.put("totalViews", movieStats.getOrDefault("totalViews", 0));
        dashboard.put("views24h", movieStats.getOrDefault("views24h", 0));
        dashboard.put("uniqueViewers24h", movieStats.getOrDefault("uniqueViewers24h", 0));
        dashboard.put("totalWatchHours", movieStats.getOrDefault("totalWatchHours", 0));

        // Derived KPIs
        long totalUsers = ((Number) userStats.getOrDefault("totalUsers", 0)).longValue();
        long activeUsers = ((Number) userStats.getOrDefault("activeUsers24h", 0)).longValue();
        double engagementRate = totalUsers == 0 ? 0 : (activeUsers * 100.0 / totalUsers);
        dashboard.put("engagementRate", Math.round(engagementRate * 10) / 10.0);

        long views24h = ((Number) movieStats.getOrDefault("views24h", 0)).longValue();
        long uniqueViewers = ((Number) movieStats.getOrDefault("uniqueViewers24h", 0)).longValue();
        double avgViewsPerUser = uniqueViewers == 0 ? 0 : (views24h * 1.0 / uniqueViewers);
        dashboard.put("avgViewsPerUser", Math.round(avgViewsPerUser * 10) / 10.0);

        dashboard.put("viewsByHour", movieStats.get("viewsByHour"));
        dashboard.put("topMovies", movieStats.get("topMovies"));
        dashboard.put("timestamp", System.currentTimeMillis());
        return dashboard;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchStats(String url) {
        try {
            return webClient.get().uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
