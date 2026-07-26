package com.allan.task.manager.workspaceinvitation.dto;

import com.allan.task.manager.membership.MembershipModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkspaceInvitationResponseDTO(

        UUID id,
        String workspaceName,
        String invitedByName,
        MembershipModel.MembershipRole role,
        LocalDateTime expiresAt,
        boolean accepted

) {
}