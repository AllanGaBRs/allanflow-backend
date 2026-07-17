package com.allan.task.manager.checklist;

import com.allan.task.manager.board.BoardPermissionService;
import com.allan.task.manager.checklist.dto.ChecklistCreateDTO;
import com.allan.task.manager.checklist.dto.ChecklistItemCreateDTO;
import com.allan.task.manager.checklist.dto.ChecklistItemResponseDTO;
import com.allan.task.manager.checklist.dto.ChecklistItemUpdateDTO;
import com.allan.task.manager.checklist.dto.ChecklistResponseDTO;
import com.allan.task.manager.checklist.dto.ChecklistUpdateDTO;
import com.allan.task.manager.checklist.mapper.ChecklistMapper;
import com.allan.task.manager.checklistItem.ChecklistItemLookupService;
import com.allan.task.manager.checklistItem.ChecklistItemModel;
import com.allan.task.manager.checklistItem.ChecklistItemRepository;
import com.allan.task.manager.task.TaskLookupService;
import com.allan.task.manager.task.TaskModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final ChecklistLookupService checklistLookupService;
    private final ChecklistItemRepository checklistItemRepository;
    private final ChecklistItemLookupService checklistItemLookupService;
    private final TaskLookupService taskLookupService;
    private final BoardPermissionService boardPermissionService;

    public ChecklistService(
            ChecklistRepository checklistRepository,
            ChecklistLookupService checklistLookupService,
            ChecklistItemRepository checklistItemRepository,
            ChecklistItemLookupService checklistItemLookupService,
            TaskLookupService taskLookupService,
            BoardPermissionService boardPermissionService
    ) {
        this.checklistRepository = checklistRepository;
        this.checklistLookupService = checklistLookupService;
        this.checklistItemRepository = checklistItemRepository;
        this.checklistItemLookupService = checklistItemLookupService;
        this.taskLookupService = taskLookupService;
        this.boardPermissionService = boardPermissionService;
    }

    @Transactional
    public ChecklistResponseDTO create(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            ChecklistCreateDTO dto,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);

        TaskModel task = taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);

        ChecklistModel checklist = new ChecklistModel();
        checklist.setTitle(dto.title().trim());
        checklist.setTask(task);

        return ChecklistMapper.toResponse(checklistRepository.save(checklist));
    }

    @Transactional(readOnly = true)
    public List<ChecklistResponseDTO> findAllByTask(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);

        return ChecklistMapper.toResponseList(
                checklistLookupService.findAllByTask(workspaceId, boardId, columnId, taskId)
        );
    }

    @Transactional
    public ChecklistResponseDTO update(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID checklistId,
            ChecklistUpdateDTO dto,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);

        ChecklistModel checklist = checklistLookupService.findChecklistInTask(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId
        );

        checklist.setTitle(dto.title().trim());

        return ChecklistMapper.toResponse(checklistRepository.save(checklist));
    }

    @Transactional
    public void delete(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID checklistId,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);

        ChecklistModel checklist = checklistLookupService.findChecklistInTask(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId
        );

        checklistRepository.delete(checklist);
    }

    @Transactional
    public ChecklistItemResponseDTO createItem(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID checklistId,
            ChecklistItemCreateDTO dto,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);

        ChecklistModel checklist = checklistLookupService.findChecklistInTask(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId
        );

        List<ChecklistItemModel> items = checklistItemLookupService.findAllByChecklist(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId
        );

        ChecklistItemModel item = new ChecklistItemModel();
        item.setContent(dto.content().trim());
        item.setChecked(false);
        item.setChecklist(checklist);

        int targetPosition = resolveTargetPosition(dto.position(), items.size());
        items.add(targetPosition, item);

        reindex(items);

        return ChecklistMapper.toItemResponse(
                checklistItemRepository.saveAll(items)
                        .stream()
                        .filter(currentItem -> currentItem == item)
                        .findFirst()
                        .orElse(item)
        );
    }

    @Transactional
    public ChecklistItemResponseDTO updateItem(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID checklistId,
            UUID itemId,
            ChecklistItemUpdateDTO dto,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);

        ChecklistItemModel item = checklistItemLookupService.findItemInChecklist(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId,
                itemId
        );

        if (dto.content() != null) {
            item.setContent(dto.content().trim());
        }

        if (dto.checked() != null) {
            item.setChecked(dto.checked());
        }

        if (dto.position() != null) {
            List<ChecklistItemModel> items = checklistItemLookupService.findAllByChecklist(
                    workspaceId,
                    boardId,
                    columnId,
                    taskId,
                    checklistId
            );

            items.removeIf(currentItem -> currentItem.getId().equals(itemId));
            items.add(resolveTargetPosition(dto.position(), items.size()), item);
            reindex(items);
            checklistItemRepository.saveAll(items);
        }

        return ChecklistMapper.toItemResponse(checklistItemRepository.save(item));
    }

    @Transactional
    public void deleteItem(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID checklistId,
            UUID itemId,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);

        ChecklistItemModel item = checklistItemLookupService.findItemInChecklist(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId,
                itemId
        );

        checklistItemRepository.delete(item);

        List<ChecklistItemModel> items = checklistItemLookupService.findAllByChecklist(
                workspaceId,
                boardId,
                columnId,
                taskId,
                checklistId
        );

        reindex(items);
        checklistItemRepository.saveAll(items);
    }

    private int resolveTargetPosition(Integer requestedPosition, int size) {
        if (requestedPosition == null) {
            return size;
        }

        return Math.max(0, Math.min(requestedPosition, size));
    }

    private void reindex(List<ChecklistItemModel> items) {
        for (int position = 0; position < items.size(); position++) {
            items.get(position).setPosition(position);
        }
    }
}
