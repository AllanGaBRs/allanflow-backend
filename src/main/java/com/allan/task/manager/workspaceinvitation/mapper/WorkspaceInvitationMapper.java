package com.allan.task.manager.workspaceinvitation.mapper;

import com.allan.task.manager.workspaceinvitation.WorkspaceInvitationModel;
import com.allan.task.manager.workspaceinvitation.dto.WorkspaceInvitationResponseDTO;

public class WorkspaceInvitationMapper {

    public static WorkspaceInvitationResponseDTO toResponse(
            WorkspaceInvitationModel invitation
    ) {
        return new WorkspaceInvitationResponseDTO(
                invitation.getId(),
                invitation.getWorkspace().getName(),
                invitation.getInvitedBy().getName(),
                invitation.getRole(),
                invitation.getExpiresAt(),
                invitation.getAcceptedAt() != null
        );
    }
}
