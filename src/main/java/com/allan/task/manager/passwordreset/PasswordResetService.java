package com.allan.task.manager.passwordreset;

import com.allan.task.manager.passwordreset.dto.ForgotPasswordRequestDTO;
import com.allan.task.manager.passwordreset.dto.ResetPasswordRequestDTO;
import com.allan.task.manager.passwordreset.exception.InvalidPasswordResetException;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserLookupService;
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
    private static final int MAX_ATTEMPTS = 3;

    private final SecureRandom secureRandom = new SecureRandom();

    private final PasswordEncoder passwordEncoder;
    private final UserLookupService userLookupService;
    private final PasswordResetEmailService passwordResetEmailService;
    private final PasswordResetRepository passwordResetRepository;

    public PasswordResetService(
            PasswordEncoder passwordEncoder,
            UserLookupService userLookupService,
            PasswordResetEmailService passwordResetEmailService,
            PasswordResetRepository passwordResetRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userLookupService = userLookupService;
        this.passwordResetEmailService = passwordResetEmailService;
        this.passwordResetRepository = passwordResetRepository;
    }

    @Transactional(
            noRollbackFor = InvalidPasswordResetException.class
    )
    public void resetPassword(ResetPasswordRequestDTO request) {

        String normalizedEmail = normalizeEmail(request.email());

        UserModel user = userLookupService
                .findOptionalActiveByEmail(normalizedEmail)
                .orElseThrow(InvalidPasswordResetException::new);

        PasswordResetModel passwordReset = passwordResetRepository
                .findByUser(user)
                .orElseThrow(InvalidPasswordResetException::new);

        validatePasswordReset(
                passwordReset,
                request.code()
        );

        user.setPassword(
                passwordEncoder.encode(request.newPassword())
        );

        passwordReset.setUsedAt(LocalDateTime.now());

    }

    @Transactional
    public void generatePasswordResetCode(ForgotPasswordRequestDTO dto) {

        String normalizedEmail = normalizeEmail(dto.email());

        UserModel user = userLookupService
                .findOptionalActiveByEmail(normalizedEmail)
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

        passwordResetEmailService.sendPasswordReset(
                user,
                code,
                EXPIRATION_MINUTES
        );
    }

    private void validatePasswordReset(
            PasswordResetModel passwordReset,
            String code
    ) {
        if (passwordReset.getUsedAt() != null) {
            throw new InvalidPasswordResetException();
        }

        if (passwordReset.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidPasswordResetException();
        }

        if (passwordReset.getAttempts() >= MAX_ATTEMPTS) {
            throw new InvalidPasswordResetException();
        }

        boolean codeMatches = passwordEncoder.matches(
                normalizeCode(code),
                passwordReset.getCodeHash()
        );

        if (!codeMatches) {
            passwordReset.setAttempts(
                    passwordReset.getAttempts() + 1
            );

            passwordResetRepository.save(passwordReset);

            throw new InvalidPasswordResetException();
        }
    }

    private String generateCode() {

        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int index = 0; index < CODE_LENGTH; index++) {
            int randomIndex = secureRandom.nextInt(CODE_CHARACTERS.length());

            code.append(CODE_CHARACTERS.charAt(randomIndex));
        }

        return code.toString();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
}
