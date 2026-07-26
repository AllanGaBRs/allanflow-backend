package com.allan.task.manager.email;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/test")
    public ResponseEntity<Void> test(
            @RequestParam String to
    ) {
        emailService.send(
                to,
                "Teste AllanFlow",
                """
                <h1>AllanFlow</h1>
                <p>O envio de e-mail está funcionando.</p>
                """
        );

        return ResponseEntity.noContent().build();
    }
}