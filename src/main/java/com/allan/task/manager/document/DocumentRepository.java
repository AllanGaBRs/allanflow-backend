package com.allan.task.manager.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentModel, UUID> {

    List<DocumentModel> findByWorkspaceIdAndBoardIdOrderByTitleAsc(
            UUID workspaceId,
            UUID boardId
    );

    Optional<DocumentModel> findByIdAndWorkspaceIdAndBoardId(
            UUID id,
            UUID workspaceId,
            UUID boardId
    );

    boolean existsByParentId(UUID parentId);
}
