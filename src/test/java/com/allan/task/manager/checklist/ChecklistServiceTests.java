package com.allan.task.manager.checklist;

import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.board.BoardPermissionService;
import com.allan.task.manager.checklist.dto.ChecklistItemCreateDTO;
import com.allan.task.manager.checklist.dto.ChecklistItemUpdateDTO;
import com.allan.task.manager.checklistItem.ChecklistItemLookupService;
import com.allan.task.manager.checklistItem.ChecklistItemModel;
import com.allan.task.manager.checklistItem.ChecklistItemRepository;
import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.factory.Factory;
import com.allan.task.manager.task.TaskLookupService;
import com.allan.task.manager.task.TaskModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChecklistServiceTests {

    @Mock
    private ChecklistRepository checklistRepository;

    @Mock
    private ChecklistLookupService checklistLookupService;

    @Mock
    private ChecklistItemRepository checklistItemRepository;

    @Mock
    private ChecklistItemLookupService checklistItemLookupService;

    @Mock
    private TaskLookupService taskLookupService;

    @Mock
    private BoardPermissionService boardPermissionService;

    @InjectMocks
    private ChecklistService checklistService;

    @Test
    void createItemShouldInsertAtRequestedPositionAndReindexItems() {
        UUID requesterId = UUID.randomUUID();
        BoardModel board = Factory.createBoardModel();
        ColumnModel column = Factory.createColumnModel(board, 0);
        TaskModel task = Factory.createTaskModel(column, 0);
        ChecklistModel checklist = Factory.createChecklistModel(task);
        ChecklistItemModel firstItem =
                Factory.createChecklistItemModel(checklist, 0);
        ChecklistItemModel secondItem =
                Factory.createChecklistItemModel(checklist, 1);
        List<ChecklistItemModel> items = new ArrayList<>(
                List.of(firstItem, secondItem)
        );
        ChecklistItemCreateDTO dto = new ChecklistItemCreateDTO(
                " New Item ",
                1
        );

        when(taskLookupService.findTaskInColumn(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId()
        )).thenReturn(task);
        when(checklistLookupService.findChecklistInTask(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId(),
                checklist.getId()
        )).thenReturn(checklist);
        when(checklistItemLookupService.findAllByChecklist(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId(),
                checklist.getId()
        )).thenReturn(items);
        when(checklistItemRepository.saveAll(items)).thenReturn(items);

        checklistService.createItem(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId(),
                checklist.getId(),
                dto,
                requesterId
        );

        assertSame(firstItem, items.get(0));
        assertEquals("New Item", items.get(1).getContent());
        assertFalse(items.get(1).getChecked());
        assertSame(secondItem, items.get(2));
        assertEquals(0, firstItem.getPosition());
        assertEquals(1, items.get(1).getPosition());
        assertEquals(2, secondItem.getPosition());
        verify(checklistItemRepository).saveAll(items);
    }

    @Test
    void updateItemShouldMoveItemAndReindexItems() {
        UUID requesterId = UUID.randomUUID();
        BoardModel board = Factory.createBoardModel();
        ColumnModel column = Factory.createColumnModel(board, 0);
        TaskModel task = Factory.createTaskModel(column, 0);
        ChecklistModel checklist = Factory.createChecklistModel(task);
        ChecklistItemModel firstItem =
                Factory.createChecklistItemModel(checklist, 0);
        ChecklistItemModel secondItem =
                Factory.createChecklistItemModel(checklist, 1);
        ChecklistItemModel movedItem =
                Factory.createChecklistItemModel(checklist, 2);
        List<ChecklistItemModel> items = new ArrayList<>(
                List.of(firstItem, secondItem, movedItem)
        );
        ChecklistItemUpdateDTO dto = new ChecklistItemUpdateDTO(
                null,
                null,
                0
        );

        when(taskLookupService.findTaskInColumn(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId()
        )).thenReturn(task);
        when(checklistItemLookupService.findItemInChecklist(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId(),
                checklist.getId(),
                movedItem.getId()
        )).thenReturn(movedItem);
        when(checklistItemLookupService.findAllByChecklist(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId(),
                checklist.getId()
        )).thenReturn(items);
        when(checklistItemRepository.save(movedItem)).thenReturn(movedItem);

        checklistService.updateItem(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId(),
                checklist.getId(),
                movedItem.getId(),
                dto,
                requesterId
        );

        assertSame(movedItem, items.get(0));
        assertSame(firstItem, items.get(1));
        assertSame(secondItem, items.get(2));
        assertEquals(0, movedItem.getPosition());
        assertEquals(1, firstItem.getPosition());
        assertEquals(2, secondItem.getPosition());
        verify(checklistItemRepository).saveAll(items);
    }
}
