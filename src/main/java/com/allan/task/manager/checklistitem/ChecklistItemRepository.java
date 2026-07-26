package com.allan.task.manager.checklistitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItemModel, UUID> {

    List<ChecklistItemModel> findAllByChecklistIdAndChecklistTaskIdAndChecklistTaskColumnIdAndChecklistTaskBoardIdAndChecklistTaskWorkspaceIdOrderByPositionAsc(
            UUID checklistId,
            UUID taskId,
            UUID columnId,
            UUID boardId,
            UUID workspaceId
    );

    Optional<ChecklistItemModel> findByIdAndChecklistIdAndChecklistTaskIdAndChecklistTaskColumnIdAndChecklistTaskBoardIdAndChecklistTaskWorkspaceId(
            UUID itemId,
            UUID checklistId,
            UUID taskId,
            UUID columnId,
            UUID boardId,
            UUID workspaceId
    );

    @Query("""
        SELECT COALESCE(MAX(i.position), 0)
        FROM ChecklistItemModel i
        WHERE i.checklist.id = :checklistId
          AND i.checklist.task.id = :taskId
          AND i.checklist.task.column.id = :columnId
          AND i.checklist.task.board.id = :boardId
          AND i.checklist.task.workspace.id = :workspaceId
    """)
    Integer findMaxPosition(
            UUID workspaceId,
            UUID boardId,
            UUID columnId,
            UUID taskId,
            UUID checklistId
    );
}
