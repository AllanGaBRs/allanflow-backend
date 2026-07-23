package com.allan.task.manager.task;

import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.board.BoardPermissionService;
import com.allan.task.manager.client.ClientLookupService;
import com.allan.task.manager.column.ColumnLookupService;
import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.factory.Factory;
import com.allan.task.manager.label.LabelLookupService;
import com.allan.task.manager.membership.MembershipLookupService;
import com.allan.task.manager.task.dto.TaskCreateDTO;
import com.allan.task.manager.task.dto.TaskMoveDTO;
import com.allan.task.manager.task.dto.TaskResponseDTO;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MembershipLookupService membershipLookupService;

    @Mock
    private BoardPermissionService boardPermissionService;

    @Mock
    private LabelLookupService labelLookupService;

    @Mock
    private ColumnLookupService columnLookupService;

    @Mock
    private ClientLookupService clientLookupService;

    @Mock
    private WorkspacePermissionService workspacePermissionService;

    @Mock
    private TaskLookupService taskLookupService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createShouldUseMediumPriorityWhenPriorityIsNull() {
        UUID requesterId = UUID.randomUUID();
        BoardModel board = Factory.createBoardModel();
        ColumnModel column = Factory.createColumnModel(board, 0);
        TaskCreateDTO dto = new TaskCreateDTO(
                "Test Task",
                "Test Description",
                null,
                null,
                Set.of(),
                Set.of(),
                null
        );

        when(columnLookupService.findColumnInBoard(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId()
        )).thenReturn(column);
        when(membershipLookupService.findActiveUsersInWorkspaceByIds(
                board.getWorkspace().getId(),
                dto.assignees()
        )).thenReturn(List.of());
        when(labelLookupService.findAllInBoard(
                board.getWorkspace().getId(),
                board.getId(),
                dto.labels()
        )).thenReturn(List.of());
        when(taskLookupService.resolvePosition(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId()
        )).thenReturn(0);
        when(taskRepository.save(any(TaskModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponseDTO response = taskService.create(
                dto,
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                requesterId
        );

        assertEquals(TaskModel.Priority.MEDIUM, response.priority());
        verify(taskRepository).save(any(TaskModel.class));
    }

    @Test
    void moveTaskShouldReorderTasksInSameColumn() {
        UUID requesterId = UUID.randomUUID();
        BoardModel board = Factory.createBoardModel();
        ColumnModel column = Factory.createColumnModel(board, 0);
        TaskModel firstTask = Factory.createTaskModel(column, 0);
        TaskModel movedTask = Factory.createTaskModel(column, 1);
        TaskModel thirdTask = Factory.createTaskModel(column, 2);
        List<TaskModel> tasks = new ArrayList<>(
                List.of(firstTask, movedTask, thirdTask)
        );
        TaskMoveDTO dto = new TaskMoveDTO(column.getId(), 0);

        when(columnLookupService.findColumnInBoard(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId()
        )).thenReturn(column);
        when(taskLookupService.findTaskInColumn(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                movedTask.getId()
        )).thenReturn(movedTask);
        when(taskRepository
                .findAllByWorkspaceIdAndBoardIdAndColumnIdOrderByPositionAsc(
                        board.getWorkspace().getId(),
                        board.getId(),
                        column.getId()
                )).thenReturn(tasks);

        taskService.moveTask(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                movedTask.getId(),
                dto,
                requesterId
        );

        assertSame(movedTask, tasks.get(0));
        assertSame(firstTask, tasks.get(1));
        assertSame(thirdTask, tasks.get(2));
        assertEquals(0, movedTask.getPosition());
        assertEquals(1, firstTask.getPosition());
        assertEquals(2, thirdTask.getPosition());
        verify(taskRepository).saveAll(tasks);
    }

    @Test
    void moveTaskShouldMoveTaskBetweenColumnsAndReindexBoth() {
        UUID requesterId = UUID.randomUUID();
        BoardModel board = Factory.createBoardModel();
        ColumnModel sourceColumn = Factory.createColumnModel(board, 0);
        ColumnModel targetColumn = Factory.createColumnModel(board, 1);
        TaskModel movedTask = Factory.createTaskModel(sourceColumn, 0);
        TaskModel sourceTask = Factory.createTaskModel(sourceColumn, 1);
        TaskModel targetTask = Factory.createTaskModel(targetColumn, 0);
        List<TaskModel> sourceTasks = new ArrayList<>(
                List.of(movedTask, sourceTask)
        );
        List<TaskModel> targetTasks = new ArrayList<>(List.of(targetTask));
        TaskMoveDTO dto = new TaskMoveDTO(targetColumn.getId(), 1);

        when(columnLookupService.findColumnInBoard(
                board.getWorkspace().getId(),
                board.getId(),
                sourceColumn.getId()
        )).thenReturn(sourceColumn);
        when(columnLookupService.findColumnInBoard(
                board.getWorkspace().getId(),
                board.getId(),
                targetColumn.getId()
        )).thenReturn(targetColumn);
        when(taskLookupService.findTaskInColumn(
                board.getWorkspace().getId(),
                board.getId(),
                sourceColumn.getId(),
                movedTask.getId()
        )).thenReturn(movedTask);
        when(taskRepository
                .findAllByWorkspaceIdAndBoardIdAndColumnIdOrderByPositionAsc(
                        board.getWorkspace().getId(),
                        board.getId(),
                        sourceColumn.getId()
                )).thenReturn(sourceTasks);
        when(taskRepository
                .findAllByWorkspaceIdAndBoardIdAndColumnIdOrderByPositionAsc(
                        board.getWorkspace().getId(),
                        board.getId(),
                        targetColumn.getId()
                )).thenReturn(targetTasks);

        taskService.moveTask(
                board.getWorkspace().getId(),
                board.getId(),
                sourceColumn.getId(),
                movedTask.getId(),
                dto,
                requesterId
        );

        assertEquals(List.of(sourceTask), sourceTasks);
        assertEquals(0, sourceTask.getPosition());
        assertEquals(List.of(targetTask, movedTask), targetTasks);
        assertEquals(0, targetTask.getPosition());
        assertEquals(1, movedTask.getPosition());
        assertSame(targetColumn, movedTask.getColumn());
        verify(taskRepository).saveAll(sourceTasks);
        verify(taskRepository).saveAll(targetTasks);
    }
}
