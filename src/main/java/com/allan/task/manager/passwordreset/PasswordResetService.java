package com.allan.task.manager.passwordreset;

import com.allan.task.manager.auth.dto.ResetPasswordRequestDTO;
import com.allan.task.manager.email.EmailService;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class PasswordResetService {

    private static final String CODE_CHARACTERS =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private static final int CODE_LENGTH = 7;
    private static final int EXPIRATION_MINUTES = 10;

    private final SecureRandom secureRandom = new SecureRandom();

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordResetRepository passwordResetRepository;

    public PasswordResetService(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            EmailService emailService,
            PasswordResetRepository passwordResetRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordResetRepository = passwordResetRepository;
    }

    @Transactional
    public void generatePasswordResetCode(ResetPasswordRequestDTO dto) {

        String normalizedEmail = dto.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        UserModel user = userRepository
                .findByEmailAndIsActiveTrue(normalizedEmail)
                .orElse(null);

        if (user == null) {
            return;
        }

        String code = generateCode();

        PasswordResetModel passwordReset = passwordResetRepository
                .findByUser(user)
                .orElseGet(PasswordResetModel::new);

        passwordReset.setUser(user);
        passwordReset.setCodeHash(passwordEncoder.encode(code));
        passwordReset.setExpiresAt(
                LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES)
        );
        passwordReset.setUsedAt(null);
        passwordReset.setAttempts(0);

        passwordResetRepository.save(passwordReset);

        sendPasswordResetEmail(user, code);
    }

    private String generateCode() {

        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int index = 0; index < CODE_LENGTH; index++) {
            int randomIndex = secureRandom.nextInt(CODE_CHARACTERS.length());

            code.append(CODE_CHARACTERS.charAt(randomIndex));
        }

        return code.toString();
    }

    private void sendPasswordResetEmail(
            UserModel user,
            String code
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
                EXPIRATION_MINUTES
        );

        emailService.send(
                user.getEmail(),
                "Código de recuperação de senha - AllanFlow",
                html
        );
    }
}