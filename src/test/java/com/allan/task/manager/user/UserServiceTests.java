package com.allan.task.manager.user;

import com.allan.task.manager.factory.Factory;
import com.allan.task.manager.user.dto.UserChangePasswordDTO;
import com.allan.task.manager.user.dto.UserRegisterDTO;
import com.allan.task.manager.user.dto.UserResponseDTO;
import com.allan.task.manager.user.dto.UserUpdateDTO;
import com.allan.task.manager.user.exception.InvalidPasswordException;
import com.allan.task.manager.user.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserLookupService userLookupService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerShouldCreateUserWithEncodedPassword() {
        UserRegisterDTO dto = Factory.createUserRegisterDTO();

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");
        when(userRepository.save(any(UserModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = userService.register(dto);

        assertEquals(dto.name(), response.name());
        assertEquals(dto.email(), response.email());
        verify(passwordEncoder).encode(dto.password());
        verify(userRepository).save(any(UserModel.class));
    }

    @Test
    void registerShouldThrowExceptionWhenEmailAlreadyExists() {
        UserRegisterDTO dto = Factory.createUserRegisterDTO();

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.register(dto)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateShouldChangeUserNameAndEmail() {
        UserModel user = Factory.createUserModel();
        UserUpdateDTO dto = new UserUpdateDTO(
                "Updated User",
                "updated-user@example.com"
        );

        when(userLookupService.findActiveById(user.getId())).thenReturn(user);
        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        UserResponseDTO response = userService.update(user.getId(), dto);

        assertEquals(dto.name(), response.name());
        assertEquals(dto.email(), response.email());
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordShouldThrowExceptionWhenCurrentPasswordIsIncorrect() {
        UserModel user = Factory.createUserModel();
        UserChangePasswordDTO dto = new UserChangePasswordDTO(
                "wrong-password",
                "new-password"
        );

        when(userLookupService.findActiveById(user.getId())).thenReturn(user);
        when(passwordEncoder.matches(dto.currentPassword(), user.getPassword()))
                .thenReturn(false);

        assertThrows(
                InvalidPasswordException.class,
                () -> userService.changePassword(user.getId(), dto)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteShouldDeactivateUser() {
        UserModel user = Factory.createUserModel();

        when(userLookupService.findActiveById(user.getId())).thenReturn(user);

        userService.delete(user.getId());

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }
}
