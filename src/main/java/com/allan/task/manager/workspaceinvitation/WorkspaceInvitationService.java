package com.allan.task.manager.workspaceinvitation;

import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.membership.MembershipRepository;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserLookupService;
import com.allan.task.manager.workspace.WorkspaceLookupService;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import com.allan.task.manager.workspaceinvitation.dto.WorkspaceInvitationAcceptDTO;
import com.allan.task.manager.workspaceinvitation.dto.WorkspaceInvitationRequestDTO;
import com.allan.task.manager.workspaceinvitation.dto.WorkspaceInvitationResponseDTO;
import com.allan.task.manager.workspaceinvitation.exception.InvalidWorkspaceInvitationException;
import com.allan.task.manager.workspaceinvitation.mapper.WorkspaceInvitationMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
public class WorkspaceInvitationService {

    private static final String CODE_CHARACTERS =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private static final int CODE_LENGTH = 7;
    private static final int EXPIRATION_DAYS = 7;
    private static final int MAX_ATTEMPTS = 5;

    private final SecureRandom secureRandom = new SecureRandom();

    private final WorkspaceInvitationRepository invitationRepository;
    private final WorkspaceLookupService workspaceLookupService;
    private final WorkspacePermissionService workspacePermissionService;
    private final MembershipRepository membershipRepository;
    private final UserLookupService userLookupService;
    private final PasswordEncoder passwordEncoder;
    private final WorkspaceInvitationEmailService invitationEmailService;

    public WorkspaceInvitationService(
            WorkspaceInvitationRepository invitationRepository,
            WorkspaceLookupService workspaceLookupService,
            WorkspacePermissionService workspacePermissionService,
            MembershipRepository membershipRepository,
            UserLookupService userLookupService,
            PasswordEncoder passwordEncoder,
            WorkspaceInvitationEmailService invitationEmailService
    ) {
        this.invitationRepository = invitationRepository;
        this.workspaceLookupService = workspaceLookupService;
        this.workspacePermissionService = workspacePermissionService;
        this.membershipRepository = membershipRepository;
        this.userLookupService = userLookupService;
        this.passwordEncoder = passwordEncoder;
        this.invitationEmailService = invitationEmailService;
    }

    @Transactional
    public WorkspaceInvitationResponseDTO invite(
            UUID workspaceId,
            UUID authenticatedUserId,
            WorkspaceInvitationRequestDTO dto
    ) {
        workspacePermissionService.requireOwnerOrAdmin(
                workspaceId,
                authenticatedUserId
        );

        validateRole(dto.role());

        WorkspaceModel workspace = workspaceLookupService.findActiveById(workspaceId);

        UserModel invitedBy = userLookupService.findActiveById(
                authenticatedUserId
        );

        String normalizedEmail = normalizeEmail(dto.email());

        validateUserIsNotAlreadyMember(
                workspaceId,
                normalizedEmail
        );

        String code = generateCode();

        WorkspaceInvitationModel invitation = invitationRepository
                .findByWorkspaceAndEmail(workspace, normalizedEmail)
                .orElseGet(WorkspaceInvitationModel::new);

        invitation.setWorkspace(workspace);
        invitation.setInvitedBy(invitedBy);
        invitation.setEmail(normalizedEmail);
        invitation.setRole(dto.role());
        invitation.setCodeHash(passwordEncoder.encode(code));
        invitation.setExpiresAt(
                LocalDateTime.now().plusDays(EXPIRATION_DAYS)
        );
        invitation.setAcceptedAt(null);
        invitation.setAttempts(0);

        invitationRepository.save(invitation);

        invitationEmailService.sendInvitation(
                invitation,
                code,
                EXPIRATION_DAYS
        );

        return WorkspaceInvitationMapper.toResponse(invitation);
    }

    @Transactional(readOnly = true)
    public WorkspaceInvitationResponseDTO findInvitation(UUID invitationId) {
        WorkspaceInvitationModel invitation = invitationRepository
                .findById(invitationId)
                .orElseThrow(InvalidWorkspaceInvitationException::new);

        validateInvitationState(invitation);

        return WorkspaceInvitationMapper.toResponse(invitation);
    }

    @Transactional(
            noRollbackFor = InvalidWorkspaceInvitationException.class
    )
    public void accept(
            UUID invitationId,
            UUID authenticatedUserId,
            WorkspaceInvitationAcceptDTO request
    ) {
        WorkspaceInvitationModel invitation = invitationRepository
                .findById(invitationId)
                .orElseThrow(InvalidWorkspaceInvitationException::new);

        UserModel authenticatedUser = userLookupService.findActiveById(
                authenticatedUserId
        );

        validateInvitationState(invitation);
        validateInvitationEmail(invitation, authenticatedUser);
        validateAttempts(invitation);
        validateCode(invitation, request.code());

        boolean alreadyMember = membershipRepository
                .existsByWorkspaceIdAndUserId(
                        invitation.getWorkspace().getId(),
                        authenticatedUser.getId()
                );

        if (alreadyMember) {
            throw new InvalidWorkspaceInvitationException(
                    "User is already a member of this workspace"
            );
        }

        MembershipModel membership = new MembershipModel();

        membership.setWorkspace(invitation.getWorkspace());
        membership.setUser(authenticatedUser);
        membership.setRole(invitation.getRole());

        membershipRepository.save(membership);

        invitation.setAcceptedAt(LocalDateTime.now());

        invitationRepository.save(invitation);
    }

    private void validateRole(MembershipModel.MembershipRole role) {
        if (role == MembershipModel.MembershipRole.OWNER) {
            throw new InvalidWorkspaceInvitationException(
                    "Another user cannot be invited as OWNER"
            );
        }
    }

    private void validateUserIsNotAlreadyMember(
            UUID workspaceId,
            String email
    ) {
        UserModel invitedUser = userLookupService
                .findOptionalActiveByEmail(email)
                .orElse(null);

        if (invitedUser == null) {
            return;
        }

        boolean alreadyMember = membershipRepository
                .existsByWorkspaceIdAndUserId(
                        workspaceId,
                        invitedUser.getId()
                );

        if (alreadyMember) {
            throw new InvalidWorkspaceInvitationException(
                    "This user is already a member of the workspace"
            );
        }
    }

    private void validateInvitationState(
            WorkspaceInvitationModel invitation
    ) {
        if (invitation.getAcceptedAt() != null) {
            throw new InvalidWorkspaceInvitationException();
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidWorkspaceInvitationException();
        }
    }

    private void validateInvitationEmail(
            WorkspaceInvitationModel invitation,
            UserModel authenticatedUser
    ) {
        if (!normalizeEmail(invitation.getEmail())
                .equals(normalizeEmail(authenticatedUser.getEmail()))) {
            throw new InvalidWorkspaceInvitationException(
                    "This invitation belongs to another user"
            );
        }
    }

    private void validateAttempts(
            WorkspaceInvitationModel invitation
    ) {
        if (invitation.getAttempts() >= MAX_ATTEMPTS) {
            throw new InvalidWorkspaceInvitationException();
        }
    }

    private void validateCode(
            WorkspaceInvitationModel invitation,
            String code
    ) {
        String normalizedCode = normalizeCode(code);

        boolean matches = passwordEncoder.matches(
                normalizedCode,
                invitation.getCodeHash()
        );

        if (!matches) {
            invitation.setAttempts(
                    invitation.getAttempts() + 1
            );

            invitationRepository.save(invitation);

            throw new InvalidWorkspaceInvitationException();
        }
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int index = 0; index < CODE_LENGTH; index++) {
            int randomIndex = secureRandom.nextInt(
                    CODE_CHARACTERS.length()
            );

            code.append(
                    CODE_CHARACTERS.charAt(randomIndex)
            );
        }

        return code.toString();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
}