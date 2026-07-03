package com.allan.task.manager.client;


import com.allan.task.manager.shared.Auditable;
import com.allan.task.manager.task.TaskModel;
import com.allan.task.manager.workspace.WorkspaceModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ClientModel extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String email;

    private String phone;

    private String company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceModel workspace;

    @OneToMany(mappedBy = "client")
    private Set<TaskModel> tasks = new HashSet<>();
}
