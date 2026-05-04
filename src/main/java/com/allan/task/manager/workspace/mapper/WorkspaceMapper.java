package com.allan.task.manager.workspace.mapper;

import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.dto.WorkspaceResponseDTO;

import java.util.List;

public class WorkspaceMapper {

    private WorkspaceMapper() {
    }

    public static WorkspaceResponseDTO toResponse(
            WorkspaceModel workspace,
            MembershipModel membership
    ) {
        return new WorkspaceResponseDTO(
                workspace.getId(),
                workspace.getName(),
                workspace.getSlug(),
                workspace.getOwner().getId(),
                membership.getRole()
        );
    }

    public static List<WorkspaceResponseDTO> toResponseList(
            List<MembershipModel> memberships
    ) {
        return memberships.stream()
                .map(membership -> toResponse(
                        membership.getWorkspace(),
                        membership
                ))
                .toList();
    }
}