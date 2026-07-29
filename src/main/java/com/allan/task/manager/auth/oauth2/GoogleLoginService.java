package com.allan.task.manager.auth.oauth2;

import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class GoogleLoginService {

    private final UserRepository userRepository;

    public UserModel findOrCreateGoogleUser(
            String googleSubject,
            String email,
            String name
    ) {
        email = email.trim().toLowerCase(Locale.ROOT);

        UserModel user = userRepository
                .findByGoogleSubject(googleSubject)
                .orElse(null);

        if (user != null) {
            validateUserIsActive(user);
            return user;
        }

        user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user != null) {
            validateUserIsActive(user);

            user.setGoogleSubject(googleSubject);

            return userRepository.save(user);
        }

        user = new UserModel();

        user.setName(name);
        user.setEmail(email);
        user.setGoogleSubject(googleSubject);
        user.setRole(UserModel.Role.ROLE_USER);

        return userRepository.save(user);
    }

    private void validateUserIsActive(UserModel user) {
        if (!user.isActive()) {
            throw new DisabledException(
                    "Esta conta está desativada."
            );
        }
    }
}