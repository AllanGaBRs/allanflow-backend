package com.allan.task.manager.board;

import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.label.LabelModel;
import com.allan.task.manager.shared.Auditable;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.workspace.WorkspaceModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardModel extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceModel workspace;

    @OneToMany(mappedBy = "board")
    private List<ColumnModel> columns;

    @OneToMany(mappedBy = "board")
    private Set<LabelModel> labels = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "tb_board_members",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "user_id"})
    )
    @Builder.Default
    private Set<UserModel> members = new HashSet<>();
}
