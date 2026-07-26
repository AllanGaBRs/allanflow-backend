package com.allan.task.manager.workspaceinvitation;

import com.allan.task.manager.workspace.WorkspaceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceInvitationRepository
        extends JpaRepository<WorkspaceInvitationModel, UUID> {

    Optional<WorkspaceInvitationModel> findByWorkspaceAndEmail(
            WorkspaceModel workspace,
            String email
    );
}