package com.allan.task.manager.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<BoardModel, UUID> {

    boolean existsByNameAndWorkspaceId(
            String name,
            UUID workspaceId
    );

    boolean existsByNameAndWorkspaceIdAndIdNot(
            String name,
            UUID workspaceId,
            UUID id
    );

    Optional<BoardModel> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    @Query("""
            SELECT DISTINCT b
            FROM BoardModel b
            LEFT JOIN FETCH b.columns c
            WHERE b.workspace.id = :workspaceId
            ORDER BY b.name ASC
            """)
    List<BoardModel> findByWorkspaceIdWithColumns(UUID workspaceId);

    @Query("""
            SELECT b
            FROM BoardModel b
            LEFT JOIN FETCH b.columns
            WHERE b.id = :id
            AND b.workspace.id = :workspaceId
            """)
    Optional<BoardModel> findByIdAndWorkspaceIdWithColumns(UUID id, UUID workspaceId);

}
