package com.allan.task.manager.board;

import com.allan.task.manager.board.dto.BoardMemberResponseDTO;
import com.allan.task.manager.board.exceptions.BoardMemberAlreadyExistsException;
import com.allan.task.manager.board.exceptions.BoardMemberNotFoundException;
import com.allan.task.manager.board.mapper.BoardMemberMapper;
import com.allan.task.manager.membership.MembershipLookupService;
import com.allan.task.manager.membership.MembershipModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BoardMemberService {

    private final BoardLookupService boardLookupService;
    private final BoardPermissionService boardPermissionService;
    private final BoardRepository boardRepository;
    private final MembershipLookupService membershipLookupService;

    public BoardMemberService(BoardLookupService boardLookupService,
                              BoardPermissionService boardPermissionService,
                              BoardRepository boardRepository,
                              MembershipLookupService membershipLookupService) {
        this.boardLookupService = boardLookupService;
        this.boardPermissionService = boardPermissionService;
        this.boardRepository = boardRepository;
        this.membershipLookupService = membershipLookupService;
    }

    @Transactional
    public BoardMemberResponseDTO addMember(UUID workspaceId, UUID boardId, UUID userId, UUID requesterId) {
        boardPermissionService.requireBoardManagement(workspaceId, requesterId);

        MembershipModel membership = membershipLookupService.findByWorkspaceAndUser(workspaceId, userId);
        BoardModel board = boardLookupService.findInWorkspaceWithMembers(workspaceId, boardId);

        boolean added = board.getMembers().add(membership.getUser());

        if (!added) {
            throw new BoardMemberAlreadyExistsException("User is already a member of this board");
        }

        return BoardMemberMapper.toResponse(membership.getUser());
    }

    @Transactional(readOnly = true)
    public List<BoardMemberResponseDTO> findMembers(UUID workspaceId, UUID boardId, UUID requesterId) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        boardLookupService.findInWorkspace(workspaceId, boardId);

        return BoardMemberMapper.toResponseList(
                boardRepository.findMembers(workspaceId, boardId)
        );
    }

    @Transactional
    public void removeMember(UUID workspaceId, UUID boardId, UUID userId, UUID requesterId) {
        boardPermissionService.requireBoardManagement(workspaceId, requesterId);

        membershipLookupService.findByWorkspaceAndUser(workspaceId, userId);

        BoardModel board = boardLookupService.findInWorkspaceWithMembers(workspaceId, boardId);

        boolean removed = board.getMembers().removeIf(user -> user.getId().equals(userId));

        if (!removed) {
            throw new BoardMemberNotFoundException("Board member not found");
        }
    }
}
