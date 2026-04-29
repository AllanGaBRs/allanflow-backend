package com.allan.task.manager.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {

    @Query(nativeQuery = true, value = """
            SELECT
                tb_user.email AS username,
                tb_user.password,
                tb_user.role AS authority
            FROM tb_user
            WHERE tb_user.email = :email
        """)
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

    Optional<UserModel> findByEmail(String email);

    boolean existsByEmail(String email);
}