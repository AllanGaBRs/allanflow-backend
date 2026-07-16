package com.allan.task.manager.membership;

import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.workspace.WorkspaceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<MembershipModel, UUID> {

    boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    Optional<MembershipModel> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    List<MembershipModel> findByUserId(UUID userId);

    @Query("""
            SELECT m.workspace
            FROM MembershipModel m
            WHERE m.user.id = :userId
            AND m.workspace.isActive = true
            """)
    List<WorkspaceModel> findActiveWorkspacesByUserId(UUID userId);

    @Query("""
            SELECT m
            FROM MembershipModel m
            JOIN FETCH m.workspace w
            WHERE m.user.id = :userId
            AND w.isActive = true
            """)
    List<MembershipModel> findActiveMembershipsByUserId(UUID userId);

    boolean existsByUserIdAndWorkspaceId(UUID userId, UUID workspaceId);

    List<MembershipModel> findByWorkspaceId(UUID workspaceId);

    @Query("""
        SELECT m.user
        FROM MembershipModel m
        WHERE m.workspace.id = :workspaceId
          AND m.workspace.isActive = true
          AND m.user.id IN :userIds
          AND m.user.isActive = true
    """)
    List<UserModel> findActiveUsersInActiveWorkspaceByIds(
            UUID workspaceId,
            Set<UUID> userIds
    );
}