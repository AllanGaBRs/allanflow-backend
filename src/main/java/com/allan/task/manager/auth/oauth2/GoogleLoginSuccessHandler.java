package com.allan.task.manager.auth.oauth2;

import com.allan.task.manager.auth.jwt.JwtService;
import com.allan.task.manager.user.UserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoogleLoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private final GoogleLoginService googleLoginService;
    private final JwtService jwtService;
    private final boolean cookieSecure;
    private final String frontendUrl;

    public GoogleLoginSuccessHandler(
            GoogleLoginService googleLoginService,
            JwtService jwtService,
            @Value("${security.cookie.secure}") boolean cookieSecure,
            @Value("${app.frontend-url}") String frontendUrl
    ) {
        this.googleLoginService = googleLoginService;
        this.jwtService = jwtService;
        this.cookieSecure = cookieSecure;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OidcUser oidcUser =
                (OidcUser) authentication.getPrincipal();

        String googleSubject = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        UserModel user =
                googleLoginService.findOrCreateGoogleUser(
                        googleSubject,
                        email,
                        name
                );

        String accessToken =
                jwtService.generateAccessToken(user);

        ResponseCookie cookie = ResponseCookie
                .from("access_token", accessToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(jwtService.getJwtDurationSeconds())
                .sameSite("Lax")
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
        response.sendRedirect(frontendUrl);
    }
}