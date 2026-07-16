package com.allan.task.manager.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentModel, UUID> {

    List<CommentModel> findAllByTaskIdAndTaskColumnIdAndTaskBoardIdAndTaskWorkspaceIdOrderByCreatedAtAsc(
            UUID taskId,
            UUID columnId,
            UUID boardId,
            UUID workspaceId
    );

    Optional<CommentModel> findByIdAndTaskIdAndTaskColumnIdAndTaskBoardIdAndTaskWorkspaceId(
            UUID commentId,
            UUID taskId,
            UUID columnId,
            UUID boardId,
            UUID workspaceId
    );
}