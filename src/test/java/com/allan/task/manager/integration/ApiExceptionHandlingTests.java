package com.allan.task.manager.integration;

import com.allan.task.manager.factory.Factory;
import com.allan.task.manager.integration.support.ApiTestHelper;
import com.allan.task.manager.user.dto.UserRegisterDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiExceptionHandlingTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${security.client-id}")
    private String clientId;

    @Value("${security.client-secret}")
    private String clientSecret;

    private ApiTestHelper apiTestHelper;

    @BeforeEach
    void setUp() {
        apiTestHelper = new ApiTestHelper(
                mockMvc,
                objectMapper,
                clientId,
                clientSecret
        );
    }

    @Test
    void protectedRouteWithoutTokenReturnsUnauthorizedBody() throws Exception {
        mockMvc.perform(get("/workspaces/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value("/workspaces/me"));
    }

    @Test
    void protectedRouteWithInvalidTokenReturnsUnauthorizedBody() throws Exception {
        mockMvc.perform(get("/workspaces/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value("/workspaces/me"));
    }

    @Test
    void registerUserWithoutTokenIsAllowed() throws Exception {
        UserRegisterDTO dto = Factory.createUserRegisterDTO();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value(dto.name()))
                .andExpect(jsonPath("$.email").value(dto.email()));
    }

    @Test
    void getUsersWithoutTokenReturnsUnauthorizedBody() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.path").value("/users"));
    }

    @Test
    void duplicateUserReturnsConflictBody() throws Exception {
        UserRegisterDTO dto = Factory.createUserRegisterDTO();

        apiTestHelper.registerUser(dto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Email already in use"))
                .andExpect(jsonPath("$.path").value("/users"));
    }

    @Test
    void validationErrorReturnsValidationBody() throws Exception {
        UserRegisterDTO user = Factory.createUserRegisterDTO();
        String token = apiTestHelper.accessToken(user);

        mockMvc.perform(post("/workspaces")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "name": ""
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.path").value("/workspaces"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].fieldName").value("name"));
    }

    @Test
    void invalidWorkspaceUuidReturnsBadRequestBody() throws Exception {
        UserRegisterDTO user = Factory.createUserRegisterDTO();
        String token = apiTestHelper.accessToken(user);

        mockMvc.perform(get("/workspaces/not-a-uuid")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid UUID parameter: workspaceId"))
                .andExpect(jsonPath("$.path").value("/workspaces/not-a-uuid"));
    }

    @Test
    void invalidJsonReturnsBadRequestBody() throws Exception {
        UserRegisterDTO user = Factory.createUserRegisterDTO();
        String token = apiTestHelper.accessToken(user);

        mockMvc.perform(post("/workspaces")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid-json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid request body"))
                .andExpect(jsonPath("$.path").value("/workspaces"));
    }

    @Test
    void authenticatedUserCanListOwnWorkspaces() throws Exception {
        UserRegisterDTO user = Factory.createUserRegisterDTO();
        String token = apiTestHelper.accessToken(user);

        apiTestHelper.createWorkspace(token, "My Workspace");

        mockMvc.perform(get("/workspaces/me")
                        .header("Authorization", apiTestHelper.bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("My Workspace"))
                .andExpect(jsonPath("$[0].userRole").value("OWNER"));
    }
}