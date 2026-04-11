package com.allan.task.manager;

import com.allan.task.manager.role.RoleModel;
import com.allan.task.manager.role.RoleRepository;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Set;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner run(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            RoleModel adminRole = roleRepository.findByAuthority("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new RoleModel("ROLE_ADMIN")));

            RoleModel userRole = roleRepository.findByAuthority("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new RoleModel("ROLE_USER")));

            if (userRepository.count() == 0) {

                UserModel admin = new UserModel();
                admin.setName("Admin");
                admin.setEmail("admin@email.com");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setRoles(Set.of(adminRole, userRole));

                userRepository.save(admin);
            }
        };
    }
}