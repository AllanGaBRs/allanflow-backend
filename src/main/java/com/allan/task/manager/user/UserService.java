package com.allan.task.manager.user;

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

import java.util.Locale;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserLookupService userLookupService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserLookupService userLookupService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userLookupService = userLookupService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO register(UserRegisterDTO dto) {
        String email = normalizeEmail(dto.email());

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        UserModel user = new UserModel();
        user.setName(dto.name());
        user.setEmail(email);
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
        UserModel user = userLookupService.findActiveById(id);
        return UserMapper.toResponse(user);
    }

    @Transactional
    public UserResponseDTO update(UUID id, UserUpdateDTO dto) {
        UserModel user = userLookupService.findActiveById(id);
        String email = dto.email() != null ? normalizeEmail(dto.email()) : null;

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new UserAlreadyExistsException("Email already in use");
            }
            user.setEmail(email);
        }

        if (dto.name() != null) {
            user.setName(dto.name());
        }

        UserModel updatedUser = userRepository.save(user);

        return UserMapper.toResponse(updatedUser);
    }

    @Transactional
    public void changePassword(UUID id, UserChangePasswordDTO dto) {
        UserModel user = userLookupService.findActiveById(id);

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
        UserModel user = userLookupService.findActiveById(id);

        user.setActive(false);

        userRepository.save(user);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
