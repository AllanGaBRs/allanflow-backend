package com.allan.task.manager.user;

import com.allan.task.manager.user.dto.UserRegisterDTO;
import com.allan.task.manager.user.dto.UserResponseDTO;
import com.allan.task.manager.user.exception.UserAlreadyExistsException;
import com.allan.task.manager.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO register(UserRegisterDTO dto) {

        if (userRepository.existsByEmail(dto.email())) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        UserModel user = new UserModel();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(UserModel.Role.ROLE_USER);
        user.setActive(true);

        UserModel savedUser = userRepository.save(user);

        return UserMapper.toResponse(savedUser);
    }
}