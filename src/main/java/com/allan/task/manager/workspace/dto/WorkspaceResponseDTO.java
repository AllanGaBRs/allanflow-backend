package com.allan.task.manager.workspace.dto;

import com.allan.task.manager.membership.MembershipModel;

import java.util.UUID;

public record WorkspaceResponseDTO(

        UUID id,
        String name,
        String slug,
        UUID ownerId,
        MembershipModel.MembershipRole userRole

) {}