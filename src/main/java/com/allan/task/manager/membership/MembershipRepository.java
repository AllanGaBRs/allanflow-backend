package com.allan.task.manager.membership;

import com.allan.task.manager.checklistItem.ChecklistItemModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<MembershipModel, UUID> {
}
