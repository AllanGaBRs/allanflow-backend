package com.allan.task.manager.integration.support;

import com.allan.task.manager.user.dto.UserRegisterDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiTestHelper {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final String clientId;
    private final String clientSecret;

    public ApiTestHelper(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String clientId,
            String clientSecret
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
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
        String response = mockMvc.perform(post("/oauth2/token")
                        .with(httpBasic(clientId, clientSecret))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "password")
                        .param("username", dto.email())
                        .param("password", dto.password())
                        .param("scope", "read write"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("access_token").asText();
    }

    public String createWorkspace(String token, String name) throws Exception {
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

        return objectMapper.readTree(response).get("id").asText();
    }

    public void addMember(
            String ownerToken,
            String workspaceId,
            String email,
            String role
    ) throws Exception {
        mockMvc.perform(post("/workspaces/{workspaceId}/members", workspaceId)
                        .header("Authorization", bearer(ownerToken))
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