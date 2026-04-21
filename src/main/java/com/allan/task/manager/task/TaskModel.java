package com.allan.task.manager.task;

import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.checklist.ChecklistModel;
import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.shared.Auditable;

import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.workspace.WorkspaceModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskModel extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceModel workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardModel board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id", nullable = false)
    private ColumnModel column;

    @Column(nullable = false)
    private Integer position;

    @OneToMany(mappedBy = "task")
    private List<ChecklistModel> checklists;

    @ManyToMany
    @JoinTable(
            name = "task_assignees",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserModel> assignees = new HashSet<>();
}
