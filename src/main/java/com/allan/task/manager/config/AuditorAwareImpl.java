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
            String userId = jwtAuth
                    .getToken()
                    .getClaimAsString("userId");

            if (userId != null && !userId.isBlank()) {
                return Optional.of(userId);
            }
        }

        return Optional.of("SYSTEM");
    }
}