package com.allan.task.manager.workspace;

import com.allan.task.manager.workspace.exception.WorkspaceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkspaceLookupService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceLookupService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public WorkspaceModel findActiveById(UUID workspaceId) {
        return workspaceRepository.findByIdAndIsActiveTrue(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException("Workspace not found"));
    }
}
