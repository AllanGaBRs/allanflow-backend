package com.allan.task.manager.label;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface LabelRepository extends JpaRepository<LabelModel, UUID> {

    Optional<LabelModel> findByIdAndBoardIdAndBoardWorkspaceId(
            UUID id,
            UUID boardId,
            UUID workspaceId
    );

    List<LabelModel> findByBoardIdAndBoardWorkspaceId(
            UUID boardId,
            UUID workspaceId
    );

    List<LabelModel> findByBoardIdAndBoardWorkspaceIdAndIdIn(
            UUID boardId,
            UUID workspaceId,
            Set<UUID> labelIds
    );
}