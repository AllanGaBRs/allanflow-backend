package com.allan.task.manager.user;

import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.comment.CommentModel;
import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.passwordreset.PasswordResetModel;
import com.allan.task.manager.shared.Auditable;
import com.allan.task.manager.task.TaskModel;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserModel extends Auditable implements UserDetails {

    public enum Role implements GrantedAuthority {
        ROLE_USER;

        @Override
        public String getAuthority() {
            return name();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
    private String password;

    @ManyToMany(mappedBy = "assignees")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<TaskModel> tasks = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<MembershipModel> memberships = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "members")
    private Set<BoardModel> boards = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private PasswordResetModel passwordReset;

    @OneToMany(mappedBy = "author")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<CommentModel> comments = new HashSet<>();

    @Column(nullable = false)
    private boolean isActive = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }
    @Override
    public String getUsername() {
        return email;
    }
}
