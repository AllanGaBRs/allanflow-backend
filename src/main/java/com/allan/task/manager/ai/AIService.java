package com.allan.task.manager.ai;

import com.allan.task.manager.ai.dto.AIRequestDTO;
import com.allan.task.manager.ai.dto.AIResponseDTO;
import com.allan.task.manager.ai.dto.N8nChatRequestDTO;
import com.allan.task.manager.ai.dto.N8nChatResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class AIService {

    private final RestClient n8nRestClient;

    public AIService(
            @Qualifier("n8nRestClient") RestClient n8nRestClient
    ) {
        this.n8nRestClient = n8nRestClient;
    }

    public AIResponseDTO chat(
            AIRequestDTO request,
            UUID requesterId
    ) {
        N8nChatRequestDTO n8nRequest = new N8nChatRequestDTO(
                request.message().trim(),
                requesterId
        );

        try {
            N8nChatResponseDTO response = n8nRestClient
                    .post()
                    .contentType(APPLICATION_JSON)
                    .body(n8nRequest)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            (httpRequest, httpResponse) -> {
                                throw new ResponseStatusException(
                                        BAD_GATEWAY,
                                        "AI service returned an error"
                                );
                            }
                    )
                    .body(N8nChatResponseDTO.class);

            if (
                    response == null
                            || response.answer() == null
                            || response.answer().isBlank()
            ) {
                throw new ResponseStatusException(
                        BAD_GATEWAY,
                        "AI service returned an empty response"
                );
            }

            return new AIResponseDTO(response.answer());

        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    BAD_GATEWAY,
                    "AI service is temporarily unavailable",
                    exception
            );
        }
    }
}