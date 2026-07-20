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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiBusinessRulesTests {

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
    void userOutsideWorkspaceCannotAccessWorkspaceDetails() throws Exception {
        UserRegisterDTO owner = Factory.createUserRegisterDTO();
        UserRegisterDTO outsider = Factory.createUserRegisterDTO();

        String ownerToken = apiTestHelper.accessToken(owner);
        String outsiderToken = apiTestHelper.accessToken(outsider);

        String workspaceId = apiTestHelper.createWorkspace(ownerToken, "Private Workspace");

        mockMvc.perform(get("/workspaces/{workspaceId}", workspaceId)
                        .header("Authorization", apiTestHelper.bearer(outsiderToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("You do not have access to this workspace"));
    }

    @Test
    void memberCannotCreateBoard() throws Exception {
        UserRegisterDTO owner = Factory.createUserRegisterDTO();
        UserRegisterDTO member = Factory.createUserRegisterDTO();

        String ownerToken = apiTestHelper.accessToken(owner);
        String memberToken = apiTestHelper.accessToken(member);

        String workspaceId = apiTestHelper.createWorkspace(
                ownerToken,
                "Workspace With Member"
        );

        apiTestHelper.addMember(
                ownerToken,
                workspaceId,
                member.email(),
                "MEMBER"
        );

        mockMvc.perform(post("/workspaces/{workspaceId}/boards", workspaceId)
                        .header("Authorization", apiTestHelper.bearer(memberToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Board Test",
                          "description": "Should fail"
                        }
                        """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("You do not have permission to perform this action"));
    }

    @Test
    void memberCannotAddAnotherMember() throws Exception {
        UserRegisterDTO owner = Factory.createUserRegisterDTO();
        UserRegisterDTO member = Factory.createUserRegisterDTO();
        UserRegisterDTO anotherMember = Factory.createUserRegisterDTO();

        String ownerToken = apiTestHelper.accessToken(owner);
        String memberToken = apiTestHelper.accessToken(member);
        apiTestHelper.accessToken(anotherMember);

        String workspaceId = apiTestHelper.createWorkspace(
                ownerToken,
                "Workspace Member Cannot Invite"
        );

        apiTestHelper.addMember(
                ownerToken,
                workspaceId,
                member.email(),
                "MEMBER"
        );

        mockMvc.perform(post("/workspaces/{workspaceId}/members", workspaceId)
                        .header("Authorization", apiTestHelper.bearer(memberToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "%s",
                          "role": "MEMBER"
                        }
                        """.formatted(anotherMember.email())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("You do not have permission to perform this action"));
    }

    @Test
    void adminCanCreateBoard() throws Exception {
        UserRegisterDTO owner = Factory.createUserRegisterDTO();
        UserRegisterDTO admin = Factory.createUserRegisterDTO();

        String ownerToken = apiTestHelper.accessToken(owner);
        String adminToken = apiTestHelper.accessToken(admin);

        String workspaceId = apiTestHelper.createWorkspace(
                ownerToken,
                "Workspace With Admin"
        );

        apiTestHelper.addMember(
                ownerToken,
                workspaceId,
                admin.email(),
                "ADMIN"
        );

        mockMvc.perform(post("/workspaces/{workspaceId}/boards", workspaceId)
                        .header("Authorization", apiTestHelper.bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Admin Board",
                          "description": "Admin can create this board"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Admin Board"));
    }
}