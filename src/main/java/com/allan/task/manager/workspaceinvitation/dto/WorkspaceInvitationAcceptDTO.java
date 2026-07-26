package com.allan.task.manager.workspaceinvitation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkspaceInvitationAcceptDTO(

        @NotBlank
        @Size(min = 7, max = 7)
        String code

) {
}