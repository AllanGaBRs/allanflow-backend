package com.allan.task.manager.checklistItem;

import com.allan.task.manager.checklist.exception.ChecklistItemNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChecklistItemLookupService {

    private final ChecklistItemRepository checklistItemRepository;

    public ChecklistItemLookupService(ChecklistItemRepository checklistItemRepository) {
        this.checklistItemRepository = checklistItemRepository;
    }

    public ChecklistItemModel findItemInChecklist(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID checklistId,
            UUID itemId
    ) {
        return checklistItemRepository.findByIdAndChecklistIdAndChecklistTaskIdAndChecklistTaskColumnIdAndChecklistTaskBoardIdAndChecklistTaskWorkspaceId(
                        itemId,
                        checklistId,
                        taskId,
                        columnId,
                        boardId,
                        workspaceId
                )
                .orElseThrow(() -> new ChecklistItemNotFoundException("Checklist item not found"));
    }

    public List<ChecklistItemModel> findAllByChecklist(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID checklistId
    ) {
        return checklistItemRepository.findAllByChecklistIdAndChecklistTaskIdAndChecklistTaskColumnIdAndChecklistTaskBoardIdAndChecklistTaskWorkspaceIdOrderByPositionAsc(
                checklistId,
                taskId,
                columnId,
                boardId,
                workspaceId
        );
    }

    public Integer resolvePosition(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID checklistId
    ) {
        return checklistItemRepository.findMaxPosition(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId
        ) + 1;
    }
}
