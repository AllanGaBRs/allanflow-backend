package com.allan.task.manager.checklist;

import com.allan.task.manager.checklist.exception.ChecklistNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChecklistLookupService {

    private final ChecklistRepository checklistRepository;

    public ChecklistLookupService(ChecklistRepository checklistRepository) {
        this.checklistRepository = checklistRepository;
    }

    public ChecklistModel findChecklistInTask(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID checklistId
    ) {
        return checklistRepository.findByIdAndTaskIdAndTaskColumnIdAndTaskBoardIdAndTaskWorkspaceId(
                        checklistId,
                        taskId,
                        columnId,
                        boardId,
                        workspaceId
                )
                .orElseThrow(() -> new ChecklistNotFoundException("Checklist not found"));
    }

    public List<ChecklistModel> findAllByTask(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId
    ) {
        return checklistRepository.findAllByTaskIdAndTaskColumnIdAndTaskBoardIdAndTaskWorkspaceIdOrderByCreatedAtAsc(
                taskId,
                columnId,
                boardId,
                workspaceId
        );
    }
}
