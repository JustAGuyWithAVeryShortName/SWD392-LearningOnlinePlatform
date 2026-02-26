package com.hsp302.shared_english_e_learning_path.config;

import com.hsp302.shared_english_e_learning_path.domain.entities.User;
import com.hsp302.shared_english_e_learning_path.domain.enums.AgeGroup;
import com.hsp302.shared_english_e_learning_path.domain.enums.Role;
import com.hsp302.shared_english_e_learning_path.domain.enums.UserStatus;
import com.hsp302.shared_english_e_learning_path.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    //private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User user = User.builder()
                        .username("admin")
                        .password(("StrongPassword!"))
                        .role(Role.ADMIN)
                        .status(UserStatus.ACTIVE)
                        .build();
                userRepository.save(user);
                log.warn("Admin account - Username: admin and Password: StrongPassword!");
            }
        };
    }
}
