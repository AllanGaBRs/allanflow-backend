package com.allan.task.manager.comment;

import com.allan.task.manager.comment.dto.CommentCreateDTO;
import com.allan.task.manager.comment.dto.CommentResponseDTO;
import com.allan.task.manager.comment.dto.CommentUpdateDTO;
import com.allan.task.manager.comment.mapper.CommentMapper;
import com.allan.task.manager.task.TaskLookupService;
import com.allan.task.manager.task.TaskModel;
import com.allan.task.manager.user.UserLookupService;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLookupService commentLookupService;
    private final TaskLookupService taskLookupService;
    private final UserLookupService userLookupService;
    private final WorkspacePermissionService workspacePermissionService;

    public CommentService(
            CommentRepository commentRepository,
            CommentLookupService commentLookupService,
            TaskLookupService taskLookupService,
            UserLookupService userLookupService,
            WorkspacePermissionService workspacePermissionService
    ) {
        this.commentRepository = commentRepository;
        this.commentLookupService = commentLookupService;
        this.taskLookupService = taskLookupService;
        this.userLookupService = userLookupService;
        this.workspacePermissionService = workspacePermissionService;
    }

    @Transactional
    public CommentResponseDTO create(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            CommentCreateDTO dto,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        TaskModel task = taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);
        UserModel author = userLookupService.findActiveById(requesterId);

        CommentModel comment = new CommentModel();
        comment.setContent(dto.content());
        comment.setTask(task);
        comment.setAuthor(author);

        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponseDTO update(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID commentId,
            CommentUpdateDTO dto,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);

        CommentModel comment = commentLookupService.findCommentInTask(workspaceId, boardId, columnId, taskId, commentId);

        boolean isAuthor = comment.getAuthor()
                .getId()
                .equals(requesterId);

        if (!isAuthor) {
            workspacePermissionService.requireOwner(workspaceId, requesterId);
        }

        comment.setContent(dto.content());

        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public CommentResponseDTO findById(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID commentId,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);

        CommentModel comment = commentLookupService.findCommentInTask(workspaceId, boardId, columnId, taskId, commentId);

        return CommentMapper.toResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> findAllByTask(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);

        return commentLookupService.findAllByTask(workspaceId, boardId, columnId, taskId)
                .stream()
                .map(CommentMapper::toResponse)
                .toList();
    }

    @Transactional
    public void delete(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID commentId,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        TaskModel task = taskLookupService.findTaskInColumn(workspaceId, boardId, columnId, taskId);

        CommentModel comment = commentLookupService.findCommentInTask(workspaceId, boardId, columnId, taskId, commentId);

        boolean isTaskOwner = task.getAssignees()
                .stream()
                .anyMatch(user -> user.getId().equals(requesterId));

        if (!isTaskOwner) {
            workspacePermissionService.requireOwner(workspaceId, requesterId);
        }

        commentRepository.delete(comment);
    }
}