package com.allan.task.manager.factory;

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
}