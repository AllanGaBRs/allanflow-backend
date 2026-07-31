package com.allan.task.manager.ai;

import com.allan.task.manager.ai.dto.AIRequestDTO;
import com.allan.task.manager.ai.dto.AIResponseDTO;
import com.allan.task.manager.config.annotation.CurrentUserId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AIResponseDTO> chat(
            @Valid @RequestBody AIRequestDTO request,
            @CurrentUserId UUID requesterId
    ) {
        return ResponseEntity.ok(
                aiService.chat(
                        request,
                        requesterId
                )
        );
    }
}