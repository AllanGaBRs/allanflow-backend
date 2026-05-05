package com.allan.task.manager.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("SYSTEM");
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();

            String subject = jwt.getSubject();

            if (subject != null && !subject.isBlank()) {
                return Optional.of(subject);
            }
        }

        String name = authentication.getName();

        if (name == null || name.isBlank()) {
            return Optional.of("SYSTEM");
        }

        return Optional.of(name);
    }
}