package com.allan.task.manager.comment;

import com.allan.task.manager.comment.exception.CommentNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentLookupService {

    private final CommentRepository commentRepository;

    public CommentLookupService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentModel findCommentInTask(UUID workspaceId, UUID boardId, UUID columnId, UUID taskId, UUID commentId) {
        return commentRepository.findByIdAndTaskIdAndTaskColumnIdAndTaskBoardIdAndTaskWorkspaceId(
                commentId, taskId, columnId, boardId, workspaceId
        )
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
    }

    public List<CommentModel> findAllByTask(UUID workspaceId, UUID boardId, UUID columnId, UUID taskId) {
        return commentRepository.findAllByTaskIdAndTaskColumnIdAndTaskBoardIdAndTaskWorkspaceIdOrderByCreatedAtAsc(
                taskId, columnId, boardId, workspaceId
        );
    }
}