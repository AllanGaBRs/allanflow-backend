package com.allan.task.manager.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkspaceCreateDTO(

        @NotBlank(message = "Workspace name is required")
        @Size(min = 2, max = 120, message = "Workspace name must be between 2 and 120 characters")
        String name

) {
}