package com.allan.task.manager.task;

import com.allan.task.manager.checklistItem.ChecklistItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, UUID> {

    Optional<TaskModel> findByIdAndColumnId(
            UUID taskId,
            UUID columnId
    );

    @Query("""
        SELECT COALESCE(MAX(t.position), 0)
        FROM TaskModel t
        WHERE t.column.id = :columnId
    """)
    Integer findMaxPositionByColumnId(UUID columnId);

    List<TaskModel> findAllByColumnIdOrderByPositionAsc(UUID columnId);

    Optional<TaskModel> findByIdAndWorkspaceId(
            UUID taskId,
            UUID workspaceId
    );
}
