package com.allan.task.manager.user;

import com.allan.task.manager.user.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserLookupService {

    private final UserRepository userRepository;

    public UserLookupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserModel findActiveByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserModel findActiveById(UUID id) {
        return userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public List<UserModel> findAllByIds(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<UserModel> users = userRepository.findByIdInAndIsActiveTrue(ids);

        if (users.size() != ids.size()) {
            throw new UserNotFoundException("One or more users were not found");
        }

        return users;
    }
}
