package com.allan.task.manager.task;

import com.allan.task.manager.checklistItem.ChecklistItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, UUID> {

    long countByWorkspaceIdAndArchivedFalse(UUID workspaceId);

    long countByWorkspaceIdAndArchivedFalseAndPriority(
            UUID workspaceId,
            TaskModel.Priority priority
    );

    @Query("""
        SELECT COUNT(t)
        FROM TaskModel t
        WHERE t.workspace.id = :workspaceId
          AND t.archived = false
          AND t.dueDate IS NOT NULL
          AND t.dueDate < :now
    """)
    long countOverdueByWorkspaceId(UUID workspaceId, LocalDateTime now);

    Optional<TaskModel> findByIdAndWorkspaceIdAndBoardIdAndColumnId(
            UUID taskId,
            UUID workspaceId,
            UUID boardId,
            UUID columnId
    );

    @Query("""
        SELECT COALESCE(MAX(t.position), 0)
        FROM TaskModel t
        WHERE t.workspace.id = :workspaceId
          AND t.board.id = :boardId
          AND t.column.id = :columnId
    """)
    Integer findMaxPositionByWorkspaceIdAndBoardIdAndColumnId(
            UUID workspaceId,
            UUID boardId,
            UUID columnId
    );

    List<TaskModel> findAllByWorkspaceIdAndBoardIdAndColumnIdOrderByPositionAsc(
            UUID workspaceId,
            UUID boardId,
            UUID columnId
    );

    Optional<TaskModel> findByIdAndWorkspaceId(
            UUID taskId,
            UUID workspaceId
    );
}
