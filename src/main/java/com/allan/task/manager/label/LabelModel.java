package com.allan.task.manager.label;

import com.allan.task.manager.shared.Auditable;
import com.allan.task.manager.task.TaskModel;
import com.allan.task.manager.workspace.WorkspaceModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_label")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LabelModel extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceModel workspace;

    @ManyToMany(mappedBy = "labels")
    private Set<TaskModel> tasks = new HashSet<>();

}
