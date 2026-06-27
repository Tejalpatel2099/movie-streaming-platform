package com.streaming.user.config;

import com.streaming.user.entity.User;
import com.streaming.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        userRepository.save(User.builder()
                .username("admin")
                .email("admin@stream.com")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .lastLogin(LocalDateTime.now())
                .build());

        userRepository.save(User.builder()
                .username("demo_user")
                .email("user@stream.com")
                .password(passwordEncoder.encode("user123"))
                .role(User.Role.USER)
                .lastLogin(LocalDateTime.now())
                .build());

        // Generate users with staggered creation dates for dashboard realism
        Random rand = new Random(42);
        String[] firstNames = {"alex", "sam", "jordan", "casey", "morgan", "riley",
                "taylor", "jamie", "drew", "blake", "quinn", "reese", "avery", "harper"};
        for (int i = 0; i < 150; i++) {
            String name = firstNames[rand.nextInt(firstNames.length)] + i;
            User u = User.builder()
                    .username(name)
                    .email(name + "@demo.com")
                    .password(passwordEncoder.encode("demo123"))
                    .role(User.Role.USER)
                    .build();
            u = userRepository.save(u);
            // Backdate creation for "new users (7d)" KPI variety
            u.setLastLogin(LocalDateTime.now().minusHours(rand.nextInt(72)));
            userRepository.save(u);
        }
        System.out.println(">>> Seeded admin + demo + 150 users");
    }
}
