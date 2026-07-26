package com.allan.task.manager.workspaceinvitation;

import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.shared.Auditable;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.workspace.WorkspaceModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "tb_workspace_invitations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_workspace_invitation_workspace_email",
                        columnNames = {"workspace_id", "email"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class WorkspaceInvitationModel extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceModel workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_user_id", nullable = false)
    private UserModel invitedBy;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipModel.MembershipRole role;

    @Column(nullable = false)
    private String codeHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime acceptedAt;

    @Column(nullable = false)
    private int attempts;
}