package com.allan.task.manager.checklist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistRepository extends JpaRepository<ChecklistModel, UUID> {

    List<ChecklistModel> findAllByTaskIdAndTaskColumnIdAndTaskBoardIdAndTaskWorkspaceIdOrderByCreatedAtAsc(
            UUID taskId,
            UUID columnId,
            UUID boardId,
            UUID workspaceId
    );

    Optional<ChecklistModel> findByIdAndTaskIdAndTaskColumnIdAndTaskBoardIdAndTaskWorkspaceId(
            UUID checklistId,
            UUID taskId,
            UUID columnId,
            UUID boardId,
            UUID workspaceId
    );
}
