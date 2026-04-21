package com.allan.task.manager.label;

import com.allan.task.manager.checklistItem.ChecklistItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LabelRepository extends JpaRepository<LabelModel, UUID> {
}
