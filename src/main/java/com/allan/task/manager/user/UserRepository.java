package com.allan.task.manager.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {

    boolean existsByEmail(String email);

    Optional<UserModel> findByEmail(String email);

    List<UserModel> findAllByIsActiveTrue();

    List<UserModel> findByIdInAndIsActiveTrue(Set<UUID> ids);

    Optional<UserModel> findByIdAndIsActiveTrue(UUID id);

    boolean existsByEmailAndIsActiveTrue(String email);

    Optional<UserModel> findByEmailAndIsActiveTrue(String email);

    Optional<UserModel> findByGoogleSubject(String googleSubject);

}