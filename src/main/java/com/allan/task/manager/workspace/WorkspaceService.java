package com.allan.task.manager.workspace;

import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.board.BoardRepository;
import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.column.ColumnRepository;
import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.membership.MembershipRepository;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserLookupService;
import com.allan.task.manager.workspace.dto.WorkspaceCreateDTO;
import com.allan.task.manager.workspace.dto.WorkspaceResponseDTO;
import com.allan.task.manager.workspace.dto.WorkspaceUpdateDTO;
import com.allan.task.manager.workspace.mapper.WorkspaceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final MembershipRepository membershipRepository;
    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;
    private final UserLookupService userLookupService;
    private final WorkspaceLookupService workspaceLookupService;
    private final WorkspacePermissionService workspacePermissionService;
    private final WorkspaceSlugService workspaceSlugService;

    public WorkspaceService(
            WorkspaceRepository workspaceRepository,
            MembershipRepository membershipRepository,
            BoardRepository boardRepository,
            ColumnRepository columnRepository,
            UserLookupService userLookupService,
            WorkspaceLookupService workspaceLookupService,
            WorkspacePermissionService workspacePermissionService,
            WorkspaceSlugService workspaceSlugService
    ) {
        this.workspaceRepository = workspaceRepository;
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.membershipRepository = membershipRepository;
        this.userLookupService = userLookupService;
        this.workspaceLookupService = workspaceLookupService;
        this.workspacePermissionService = workspacePermissionService;
        this.workspaceSlugService = workspaceSlugService;
    }

    @Transactional
    public WorkspaceResponseDTO create(WorkspaceCreateDTO dto, UUID requesterId) {
        UserModel owner = userLookupService.findActiveById(requesterId);

        WorkspaceModel workspace = new WorkspaceModel();
        workspace.setName(dto.name());
        workspace.setSlug(workspaceSlugService.generateUniqueSlug(dto.name()));
        workspace.setOwner(owner);
        workspace.setActive(true);
        WorkspaceModel savedWorkspace = workspaceRepository.save(workspace);

        MembershipModel membership = new MembershipModel();
        membership.setUser(owner);
        membership.setWorkspace(savedWorkspace);
        membership.setRole(MembershipModel.MembershipRole.OWNER);
        MembershipModel savedMembership = membershipRepository.save(membership);

        BoardModel board = new BoardModel();
        board.setName("Kanban");
        board.setDescription("Quadro Kanban");
        board.setWorkspace(workspace);
        boardRepository.save(board);

        ColumnModel column1 = new ColumnModel();
        column1.setName("PENDENTE");
        column1.setPosition(0);
        column1.setWorkspace(workspace);
        column1.setBoard(board);
        columnRepository.save(column1);

        ColumnModel column2 = new ColumnModel();
        column2.setName("EM ANDAMENTO");
        column2.setPosition(1);
        column2.setWorkspace(workspace);
        column2.setBoard(board);
        columnRepository.save(column2);

        ColumnModel column3 = new ColumnModel();
        column3.setName("CONCLUÍDO");
        column3.setPosition(2);
        column3.setWorkspace(workspace);
        column3.setBoard(board);
        columnRepository.save(column3);

        return WorkspaceMapper.toResponse(savedWorkspace, savedMembership);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponseDTO> findMyWorkspaces(UUID requesterId) {
        List<MembershipModel> memberships =
                membershipRepository.findActiveMembershipsByUserId(
                        requesterId
                );

        return WorkspaceMapper.toResponseList(memberships);
    }

    @Transactional(readOnly = true)
    public WorkspaceResponseDTO findById(UUID workspaceId, UUID requesterId) {
        MembershipModel membership = workspacePermissionService
                .requireMember(workspaceId, requesterId);

        WorkspaceModel workspace = workspaceLookupService.findActiveById(workspaceId);

        return WorkspaceMapper.toResponse(workspace, membership);
    }

    @Transactional
    public WorkspaceResponseDTO update(
            UUID workspaceId,
            WorkspaceUpdateDTO dto,
            UUID requesterId
    ) {
        MembershipModel membership = workspacePermissionService
                .requireOwnerOrAdmin(workspaceId, requesterId);

        WorkspaceModel workspace = workspaceLookupService.findActiveById(workspaceId);

        if (dto.name() != null && !dto.name().isBlank()) {
            workspace.setName(dto.name());
            workspace.setSlug(workspaceSlugService.generateUniqueSlug(dto.name()));
        }

        WorkspaceModel updatedWorkspace = workspaceRepository.save(workspace);

        return WorkspaceMapper.toResponse(updatedWorkspace, membership);
    }

    @Transactional
    public void delete(UUID workspaceId,  UUID requesterId) {
        workspacePermissionService.requireOwner(workspaceId, requesterId);

        WorkspaceModel workspace = workspaceLookupService.findActiveById(workspaceId);

        workspace.setActive(false);

        workspaceRepository.save(workspace);
    }
}
