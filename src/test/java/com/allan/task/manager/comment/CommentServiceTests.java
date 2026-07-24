package com.allan.task.manager.comment;

import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.board.BoardPermissionService;
import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.comment.dto.CommentUpdateDTO;
import com.allan.task.manager.comment.exception.CommentAccessDeniedException;
import com.allan.task.manager.factory.Factory;
import com.allan.task.manager.task.TaskLookupService;
import com.allan.task.manager.task.TaskModel;
import com.allan.task.manager.user.UserLookupService;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTests {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLookupService commentLookupService;

    @Mock
    private TaskLookupService taskLookupService;

    @Mock
    private UserLookupService userLookupService;

    @Mock
    private BoardPermissionService boardPermissionService;

    @Mock
    private WorkspacePermissionService workspacePermissionService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void updateShouldThrowExceptionWhenRequesterIsNotAuthor() {
        UUID requesterId = UUID.randomUUID();
        BoardModel board = Factory.createBoardModel();
        ColumnModel column = Factory.createColumnModel(board, 0);
        TaskModel task = Factory.createTaskModel(column, 0);
        UserModel author = Factory.createUserModel();
        CommentModel comment = Factory.createCommentModel(task, author);
        CommentUpdateDTO dto = new CommentUpdateDTO("Updated Comment");

        when(taskLookupService.findTaskInColumn(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId()
        )).thenReturn(task);
        when(commentLookupService.findCommentInTask(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId(),
                comment.getId()
        )).thenReturn(comment);

        assertThrows(
                CommentAccessDeniedException.class,
                () -> commentService.update(
                        board.getWorkspace().getId(),
                        board.getId(),
                        column.getId(),
                        task.getId(),
                        comment.getId(),
                        dto,
                        requesterId
                )
        );

        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteShouldRequireOwnerOrAdminWhenRequesterIsNotAuthor() {
        UUID requesterId = UUID.randomUUID();
        BoardModel board = Factory.createBoardModel();
        ColumnModel column = Factory.createColumnModel(board, 0);
        TaskModel task = Factory.createTaskModel(column, 0);
        UserModel author = Factory.createUserModel();
        CommentModel comment = Factory.createCommentModel(task, author);

        when(taskLookupService.findTaskInColumn(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId()
        )).thenReturn(task);
        when(commentLookupService.findCommentInTask(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId(),
                comment.getId()
        )).thenReturn(comment);

        commentService.delete(
                board.getWorkspace().getId(),
                board.getId(),
                column.getId(),
                task.getId(),
                comment.getId(),
                requesterId
        );

        verify(workspacePermissionService).requireOwnerOrAdmin(
                board.getWorkspace().getId(),
                requesterId
        );
        verify(commentRepository).delete(comment);
    }
}
