package com.allan.task.manager.user;

import com.allan.task.manager.config.annotation.CurrentUserId;
import com.allan.task.manager.user.dto.UserChangePasswordDTO;
import com.allan.task.manager.user.dto.UserRegisterDTO;
import com.allan.task.manager.user.dto.UserResponseDTO;
import com.allan.task.manager.user.dto.UserUpdateDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @SecurityRequirements
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO dto) {
        UserResponseDTO response = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMe(
            @Valid @RequestBody UserUpdateDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        return ResponseEntity.ok(userService.updateName(requesterId, dto));
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody UserChangePasswordDTO dto,
            @CurrentUserId UUID requesterId
    ) {
        userService.changePassword(requesterId, dto);
        return ResponseEntity.noContent().build();
    }
}
