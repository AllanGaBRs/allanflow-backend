package com.allan.task.manager.auth;

import com.allan.task.manager.passwordreset.dto.ForgotPasswordRequestDTO;
import com.allan.task.manager.passwordreset.PasswordResetService;
import com.allan.task.manager.passwordreset.dto.ResetPasswordRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordResetService passwordResetService;

    public AuthController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> user = new HashMap<>();
        user.put("id", jwt.getClaimAsString("userId"));
        user.put("email", jwt.getSubject());
        user.put("authorities", jwt.getClaimAsStringList("authorities"));

        return ResponseEntity.ok(user);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ForgotPasswordRequestDTO dto
    ) {
        passwordResetService.generatePasswordResetCode(dto);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordRequestDTO request
    ) {
        passwordResetService.resetPassword(request);

        return ResponseEntity.noContent().build();
    }
}
