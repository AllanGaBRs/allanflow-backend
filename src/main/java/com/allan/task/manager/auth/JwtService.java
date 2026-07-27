package com.allan.task.manager.auth;

import com.allan.task.manager.user.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long jwtDurationSeconds;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.duration}") long jwtDurationSeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDurationSeconds = jwtDurationSeconds;
    }

    public String generateAccessToken(UserModel user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(jwtDurationSeconds);

        List<String> authorities = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("allanflow-api")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.getEmail())
                .claim("userId", user.getId().toString())
                .claim("authorities", authorities)
                .build();

        JwsHeader header = JwsHeader
                .with(SignatureAlgorithm.RS256)
                .keyId("allanflow-key")
                .build();

        JwtEncoderParameters parameters =
                JwtEncoderParameters.from(header, claims);

        return jwtEncoder.encode(parameters).getTokenValue();
    }

    public long getJwtDurationSeconds() {
        return jwtDurationSeconds;
    }
}