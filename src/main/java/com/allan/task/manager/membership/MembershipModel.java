package com.allan.task.manager.membership;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.workspace.WorkspaceModel;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "memberships",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "workspace_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipModel {

    public enum MembershipRole {
        OWNER,
        ADMIN,
        MEMBER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceModel workspace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipRole role;

}