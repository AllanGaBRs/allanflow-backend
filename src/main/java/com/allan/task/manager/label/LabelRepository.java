package com.allan.task.manager.label;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LabelRepository extends JpaRepository<LabelModel, UUID> {

    Optional<LabelModel> findByIdAndBoardId(UUID id, UUID boardId);

    List<LabelModel> findByBoardId(UUID boardId);
}