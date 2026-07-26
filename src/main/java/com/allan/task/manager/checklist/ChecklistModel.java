package com.allan.task.manager.checklist;

import com.allan.task.manager.checklistitem.ChecklistItemModel;
import com.allan.task.manager.shared.Auditable;
import com.allan.task.manager.task.TaskModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_checklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ChecklistModel extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private TaskModel task;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<ChecklistItemModel> items = new ArrayList<>();
}
