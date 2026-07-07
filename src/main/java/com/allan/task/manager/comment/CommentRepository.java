package com.allan.task.manager.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<CommentModel, UUID> {

    List<CommentModel> findAllByTaskIdOrderByCreatedAtAsc(
            UUID taskId
    );

    Optional<CommentModel> findByIdAndTaskId(
            UUID commentId,
            UUID taskId
    );
}