package com.allan.task.manager.integration.support;

import com.allan.task.manager.auth.dto.LoginRequestDTO;
import com.allan.task.manager.user.dto.UserRegisterDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiTestHelper {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public ApiTestHelper(
            MockMvc mockMvc,
            ObjectMapper objectMapper
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public String bearer(String token) {
        return "Bearer " + token;
    }

    public String accessToken(UserRegisterDTO dto) throws Exception {
        registerUser(dto);
        return login(dto);
    }

    public void registerUser(UserRegisterDTO dto) throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    public String login(UserRegisterDTO dto) throws Exception {
        LoginRequestDTO request = new LoginRequestDTO(
                dto.email(),
                dto.password()
        );

        var response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Cookie accessTokenCookie = response.getCookie("access_token");

        assertNotNull(
                accessTokenCookie,
                "Login response should contain access_token cookie"
        );

        return accessTokenCookie.getValue();
    }

    public String createWorkspace(
            String token,
            String name
    ) throws Exception {
        String response = mockMvc.perform(post("/workspaces")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "%s"
                        }
                        """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response)
                .get("id")
                .asText();
    }

    public void addMember(
            String ownerToken,
            String workspaceId,
            String email,
            String role
    ) throws Exception {
        mockMvc.perform(post(
                        "/workspaces/{workspaceId}/members",
                        workspaceId
                )
                        .header(
                                "Authorization",
                                bearer(ownerToken)
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "%s",
                          "role": "%s"
                        }
                        """.formatted(email, role)))
                .andExpect(status().isCreated());
    }
}