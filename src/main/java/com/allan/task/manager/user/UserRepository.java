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

    @Query(nativeQuery = true, value = """
            SELECT
                tb_user.id AS id,
                tb_user.email AS username,
                tb_user.password,
                tb_user.role AS authority
            FROM tb_user
            WHERE tb_user.email = :email
                AND tb_user.is_active = true
        """)
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserModel> findByEmail(String email);

    List<UserModel> findAllByIsActiveTrue();

    List<UserModel> findByIdInAndIsActiveTrue(Set<UUID> ids);

    Optional<UserModel> findByIdAndIsActiveTrue(UUID id);

    boolean existsByEmailAndIsActiveTrue(String email);

    Optional<UserModel> findByEmailAndIsActiveTrue(String email);

}