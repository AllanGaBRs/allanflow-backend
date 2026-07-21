package com.allan.task.manager.integration.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${security.client-id}")
    private String clientId;

    @Value("${security.client-secret}")
    private String clientSecret;

    protected ApiTestHelper apiTestHelper;

    @BeforeEach
    protected void setUpApiTestHelper() {
        apiTestHelper = new ApiTestHelper(
                mockMvc,
                objectMapper,
                clientId,
                clientSecret
        );
    }

    private static final KeyPair TEST_KEY_PAIR = generateKeyPair();

    @DynamicPropertySource
    static void jwtProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "security.jwt.private-key-base64",
                () -> Base64.getEncoder()
                        .encodeToString(TEST_KEY_PAIR.getPrivate().getEncoded())
        );

        registry.add(
                "security.jwt.public-key-base64",
                () -> Base64.getEncoder()
                        .encodeToString(TEST_KEY_PAIR.getPublic().getEncoded())
        );
    }

    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "Could not generate RSA keys for integration tests",
                    e
            );
        }
    }

}