package com.allan.task.manager.passwordreset;

import com.allan.task.manager.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetRepository extends JpaRepository<PasswordResetModel, UUID> {

    Optional<PasswordResetModel> findByUser(UserModel user);

}