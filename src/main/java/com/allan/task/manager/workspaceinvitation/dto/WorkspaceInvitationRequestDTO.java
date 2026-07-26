package com.allan.task.manager.workspaceinvitation.dto;

import com.allan.task.manager.membership.MembershipModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WorkspaceInvitationRequestDTO(

        @NotBlank
        @Email
        String email,

        @NotNull
        MembershipModel.MembershipRole role

) {
}