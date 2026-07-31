package com.allan.task.manager.auth.oauth2;

import com.allan.task.manager.auth.jwt.JwtService;
import com.allan.task.manager.config.app.AppProperties;
import com.allan.task.manager.user.UserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.DisabledException;
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
    private final AppProperties appProperties;
    private final String cookieDomain;

    public GoogleLoginSuccessHandler(
            GoogleLoginService googleLoginService,
            JwtService jwtService,
            AppProperties appProperties,
            @Value("${security.cookie.secure}") boolean cookieSecure,
            @Value("${security.cookie.domain:}") String cookieDomain
    ) {
        this.googleLoginService = googleLoginService;
        this.jwtService = jwtService;
        this.cookieSecure = cookieSecure;
        this.appProperties = appProperties;
        this.cookieDomain = cookieDomain;
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

        try {
            UserModel user =
                    googleLoginService.findOrCreateGoogleUser(
                            googleSubject,
                            email,
                            name
                    );

            String accessToken =
                    jwtService.generateAccessToken(user);

            ResponseCookie.ResponseCookieBuilder cookieBuilder =
                    ResponseCookie.from("access_token", accessToken)
                            .httpOnly(true)
                            .secure(cookieSecure)
                            .sameSite("Lax")
                            .path("/")
                            .maxAge(jwtService.getJwtDurationSeconds());

            if (!cookieDomain.isBlank()) {
                cookieBuilder.domain(cookieDomain);
            }

            ResponseCookie cookie = cookieBuilder.build();

            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    cookie.toString()
            );

            response.sendRedirect(appProperties.frontendUrl());

        } catch (DisabledException exception) {
            response.sendRedirect(
                    appProperties.frontendUrl() + "/login?error=account_disabled"
            );
        }
    }
}