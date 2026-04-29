package com.allan.task.manager.user.mapper;

import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.dto.UserResponseDTO;

import java.util.List;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponseDTO toResponse(UserModel user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isActive()
        );
    }

    public static List<UserResponseDTO> toResponseList(List<UserModel> users) {
        return users.stream()
                .map(UserMapper::toResponse)
                .toList();
    }
}