package com.allan.task.manager.column;

import com.allan.task.manager.checklistItem.ChecklistItemModel;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ColumnRepository extends JpaRepository<ColumnModel, UUID> {
}
