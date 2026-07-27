package com.allan.task.manager.auth;

import com.allan.task.manager.auth.dto.LoginRequestDTO;
import com.allan.task.manager.auth.dto.LoginResponseDTO;
import com.allan.task.manager.auth.dto.LoginResultDTO;
import com.allan.task.manager.user.UserModel;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResultDTO login(LoginRequestDTO request) {
        String email = request.email()
                .trim()
                .toLowerCase();

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.password()
                        )
                );

        UserModel user = (UserModel) authentication.getPrincipal();

        String accessToken =
                jwtService.generateAccessToken(user);

        LoginResponseDTO response = new LoginResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return new LoginResultDTO(
                accessToken,
                jwtService.getJwtDurationSeconds(),
                response
        );
    }
}