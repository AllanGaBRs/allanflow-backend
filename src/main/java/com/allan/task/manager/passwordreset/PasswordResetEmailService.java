package com.allan.task.manager.passwordreset;

import com.allan.task.manager.email.EmailService;
import com.allan.task.manager.user.UserModel;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetEmailService {

    private final EmailService emailService;

    public PasswordResetEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendPasswordReset(
            UserModel user,
            String code,
            int expirationMinutes
    ) {
        String html = """
                <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <h2>Recuperação de senha</h2>

                    <p>Olá, %s!</p>

                    <p>
                        Recebemos uma solicitação para redefinir
                        a senha da sua conta no AllanFlow.
                    </p>

                    <p>Seu código de recuperação é:</p>

                    <div style="
                        font-size: 28px;
                        font-weight: bold;
                        letter-spacing: 6px;
                        margin: 24px 0;
                    ">
                        %s
                    </div>

                    <p>
                        Este código expira em %d minutos.
                    </p>

                    <p>
                        Caso você não tenha solicitado a recuperação,
                        ignore este e-mail.
                    </p>
                </div>
                """.formatted(
                user.getName(),
                code,
                expirationMinutes
        );

        emailService.send(
                user.getEmail(),
                "Código de recuperação de senha - AllanFlow",
                html
        );
    }
}
