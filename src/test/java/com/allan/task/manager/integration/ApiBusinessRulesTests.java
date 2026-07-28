package com.allan.task.manager.integration;

import com.allan.task.manager.factory.Factory;
import com.allan.task.manager.integration.support.IntegrationTest;
import com.allan.task.manager.user.dto.UserRegisterDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiBusinessRulesTests extends IntegrationTest {

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
    // TODO: Update integration tests to use the workspace invitation flow instead of direct member creation.
    /*@Test
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
    }*/
}