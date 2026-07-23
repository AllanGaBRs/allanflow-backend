package com.allan.task.manager.factory;

import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.dto.UserRegisterDTO;

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
}
