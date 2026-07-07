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

    public CommentModel findCommentInTask(
            UUID taskId,
            UUID commentId
    ) {
        return commentRepository.findByIdAndTaskId(commentId, taskId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));
    }

    public List<CommentModel> findAllByTask(
            UUID taskId
    ) {
        return commentRepository.findAllByTaskIdOrderByCreatedAtAsc(taskId);
    }
}