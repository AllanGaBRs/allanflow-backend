package com.allan.task.manager.factory;

import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.dto.UserRegisterDTO;
import com.allan.task.manager.workspace.WorkspaceModel;

import java.util.UUID;

public class Factory {

    public static UserRegisterDTO createUserRegisterDTO() {
        return new UserRegisterDTO(
                "Test User",
                "test-user-" + UUID.randomUUID() + "@example.com",
                "123456"
        );
    }

    public static UserModel createUserModel() {
        UserModel user = new UserModel();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test-user-" + UUID.randomUUID() + "@example.com");
        user.setPassword("encoded-password");
        user.setRole(UserModel.Role.ROLE_USER);
        user.setActive(true);
        return user;
    }

    public static WorkspaceModel createWorkspaceModel() {
        UserModel owner = createUserModel();

        WorkspaceModel workspace = new WorkspaceModel();
        workspace.setId(UUID.randomUUID());
        workspace.setName("Test Workspace");
        workspace.setSlug("test-workspace-" + UUID.randomUUID());
        workspace.setOwner(owner);
        workspace.setActive(true);
        return workspace;
    }

    public static MembershipModel createMembershipModel(
            MembershipModel.MembershipRole role
    ) {
        MembershipModel membership = new MembershipModel();
        membership.setId(UUID.randomUUID());
        membership.setUser(createUserModel());
        membership.setWorkspace(createWorkspaceModel());
        membership.setRole(role);
        return membership;
    }
}
