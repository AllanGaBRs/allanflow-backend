package com.allan.task.manager.checklistItem;

import com.allan.task.manager.workspace.WorkspaceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItemModel, UUID> {
}
