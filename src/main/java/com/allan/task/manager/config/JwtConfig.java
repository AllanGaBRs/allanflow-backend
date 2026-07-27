package com.allan.task.manager.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Value("${security.jwt.private-key-base64}")
    private String privateKeyBase64;

    @Value("${security.jwt.public-key-base64}")
    private String publicKeyBase64;

    @Bean
    public RSAKey rsaKey() {
        try {
            byte[] privateBytes = Base64.getDecoder()
                    .decode(privateKeyBase64);

            byte[] publicBytes = Base64.getDecoder()
                    .decode(publicKeyBase64);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            RSAPrivateKey privateKey =
                    (RSAPrivateKey) keyFactory.generatePrivate(
                            new PKCS8EncodedKeySpec(privateBytes)
                    );

            RSAPublicKey publicKey =
                    (RSAPublicKey) keyFactory.generatePublic(
                            new X509EncodedKeySpec(publicBytes)
                    );

            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID("allanflow-key")
                    .build();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to load JWT RSA keys",
                    exception
            );
        }
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAKey rsaKey) {
        JWKSource<SecurityContext> jwkSource =
                (jwkSelector, securityContext) ->
                        jwkSelector.select(new JWKSet(rsaKey));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
        return NimbusJwtDecoder
                .withPublicKey(rsaKey.toRSAPublicKey())
                .build();
    }
}