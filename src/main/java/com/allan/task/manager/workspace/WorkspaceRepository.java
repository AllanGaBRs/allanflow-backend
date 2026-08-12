package com.allan.task.manager.workspace;

import com.allan.task.manager.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceModel, UUID> {

    Optional<WorkspaceModel> findByIdAndIsActiveTrue(UUID id);

    //TODO: Necessário verificar casos de slug em workspaces inativos
    boolean existsBySlugAndIsActiveTrue(String slug);

    //boolean existsBySlug(String slug);
}
