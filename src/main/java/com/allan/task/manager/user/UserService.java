package com.allan.task.manager.user;

import com.allan.task.manager.shared.Utils;
import com.allan.task.manager.user.dto.UserChangePasswordDTO;
import com.allan.task.manager.user.dto.UserRegisterDTO;
import com.allan.task.manager.user.dto.UserResponseDTO;
import com.allan.task.manager.user.dto.UserUpdateDTO;
import com.allan.task.manager.user.exception.InvalidPasswordException;
import com.allan.task.manager.user.exception.UserAlreadyExistsException;
import com.allan.task.manager.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return UserMapper.toResponseList(userRepository.findAllByIsActiveTrue());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {
        UserModel user = Utils.findActiveUserById(userRepository, id);
        return UserMapper.toResponse(user);
    }

    @Transactional
    public UserResponseDTO update(UUID id, UserUpdateDTO dto) {
        UserModel user = Utils.findActiveUserById(userRepository, id);

        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.email())) {
                throw new UserAlreadyExistsException("Email already in use");
            }
            user.setEmail(dto.email());
        }

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        UserModel updatedUser = userRepository.save(user);

        return UserMapper.toResponse(updatedUser);
    }

    @Transactional
    public void changePassword(UUID id, UserChangePasswordDTO dto) {
        UserModel user = Utils.findActiveUserById(userRepository, id);

        boolean passwordMatches = passwordEncoder.matches(
                dto.currentPassword(),
                user.getPassword()
        );

        if (!passwordMatches) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));

        userRepository.save(user);
    }

    @Transactional
    public void delete(UUID id) {
        UserModel user = Utils.findActiveUserById(userRepository, id);

        user.setActive(false);

        userRepository.save(user);
    }
}
