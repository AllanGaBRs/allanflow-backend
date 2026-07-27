package com.allan.task.manager.auth.dto;

public record LoginResultDTO(
        String accessToken,
        long expiresIn,
        LoginResponseDTO user
) {
}
