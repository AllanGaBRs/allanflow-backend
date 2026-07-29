package com.allan.task.manager.auth.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final RateLimitRuleResolver ruleResolver;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(
            RateLimitService rateLimitService,
            RateLimitRuleResolver ruleResolver,
            ObjectMapper objectMapper
    ) {
        this.rateLimitService = rateLimitService;
        this.ruleResolver = ruleResolver;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        RateLimitRule rule = ruleResolver.resolve(
                request.getMethod(),
                request.getRequestURI()
        );

        if (rule == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = request.getRemoteAddr();

        String key = rule.name() + ":" + clientIp;

        boolean allowed = rateLimitService.tryConsume(
                key,
                rule.capacity(),
                rule.duration()
        );

        if (!allowed) {
            writeTooManyRequestsResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeTooManyRequestsResponse(
            HttpServletResponse response
    ) throws IOException {

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> body = Map.of(
                "status", HttpStatus.TOO_MANY_REQUESTS.value(),
                "error", "Too Many Requests",
                "message", "Muitas tentativas. Tente novamente mais tarde."
        );

        objectMapper.writeValue(
                response.getWriter(),
                body
        );
    }
}