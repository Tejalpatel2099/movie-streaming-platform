package com.streaming.user.controller;

import com.streaming.user.dto.AuthDtos.*;
import com.streaming.user.entity.User;
import com.streaming.user.repository.UserRepository;
import com.streaming.user.security.JwtUtil;
import com.streaming.user.service.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            return ResponseEntity.ok(authService.register(req));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            return ResponseEntity.ok(authService.login(req));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            if (!jwtUtil.isValid(token))
                return ResponseEntity.status(401).body(Map.of("valid", false));
            Claims claims = jwtUtil.parseToken(token);
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "email", claims.getSubject(),
                "role", claims.get("role"),
                "userId", claims.get("userId")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("valid", false));
        }
    }

    // Internal endpoint for analytics service
    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        return ResponseEntity.ok(Map.of(
            "totalUsers", userRepository.count(),
            "activeUsers24h", userRepository.countActiveUsersSince(dayAgo),
            "newUsers7d", userRepository.countNewUsersSince(weekAgo)
        ));
    }
}
