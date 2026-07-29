package com.allan.task.manager.auth.oauth2;

import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleLoginService {

    private final UserRepository userRepository;

    public UserModel findOrCreateGoogleUser(
            String googleSubject,
            String email,
            String name
    ) {
        UserModel user = userRepository
                .findByGoogleSubjectAndIsActiveTrue(googleSubject)
                .orElse(null);

        if (user != null) {
            return user;
        }

        user = userRepository
                .findByEmailAndIsActiveTrue(email)
                .orElse(null);

        if (user != null) {
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
}