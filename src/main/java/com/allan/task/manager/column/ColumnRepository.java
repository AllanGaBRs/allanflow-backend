package com.allan.task.manager.column;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ColumnRepository extends JpaRepository<ColumnModel, UUID> {

    List<ColumnModel> findByWorkspaceIdAndBoardIdOrderByPositionAsc(UUID workspaceId, UUID boardId);

    Optional<ColumnModel> findByIdAndWorkspaceIdAndBoardId(UUID id, UUID workspaceId, UUID boardId);

    @Query("""
            SELECT COALESCE(MAX(c.position), -1)
            FROM ColumnModel c
            WHERE c.workspace.id = :workspaceId
            AND c.board.id = :boardId
            """)
    Integer findMaxPositionByWorkspaceIdAndBoardId(UUID workspaceId, UUID boardId);
}
