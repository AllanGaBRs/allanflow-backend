package com.allan.task.manager.client;

import com.allan.task.manager.workspace.WorkspaceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientModel, UUID> {

    @Query("""
        SELECT c
        FROM ClientModel c
        WHERE c.workspace = :workspace
        AND (
            (:name IS NOT NULL AND LOWER(c.name) = LOWER(:name))
            OR (:email IS NOT NULL AND c.email = :email)
            OR (:phone IS NOT NULL AND c.phone = :phone)
        )
    """)
    List<ClientModel> findPossibleDuplicates(
            WorkspaceModel workspace,
            String name,
            String email,
            String phone
    );

    @Query("""
        SELECT c
        FROM ClientModel c
        WHERE c.workspace = :workspace
          AND c.id <> :clientId
          AND (
              (:name IS NOT NULL AND LOWER(c.name) = LOWER(:name))
              OR (:email IS NOT NULL AND LOWER(c.email) = LOWER(:email))
              OR (:phone IS NOT NULL AND c.phone = :phone)
          )
    """)
    List<ClientModel> findPossibleDuplicatesExcludingClient(
            WorkspaceModel workspace,
            UUID clientId,
            String name,
            String email,
            String phone
    );

    Optional<ClientModel> findByIdAndWorkspaceId(UUID clientId, UUID workspaceId);

    List<ClientModel> findAllByWorkspaceId(UUID workspaceId);
}
