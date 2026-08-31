package com.allan.task.manager.document;

import com.allan.task.manager.board.BoardLookupService;
import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.board.BoardPermissionService;
import com.allan.task.manager.document.dto.DocumentCreateDTO;
import com.allan.task.manager.document.dto.DocumentResponseDTO;
import com.allan.task.manager.document.exception.InvalidDocumentOperationException;
import com.allan.task.manager.document.mapper.DocumentMapper;
import com.allan.task.manager.workspace.WorkspaceLookupService;
import com.allan.task.manager.workspace.WorkspaceModel;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentLookupService documentLookupService;
    private final WorkspaceLookupService workspaceLookupService;
    private final BoardLookupService boardLookupService;
    private final BoardPermissionService boardPermissionService;

    public DocumentService(
            DocumentRepository documentRepository,
            DocumentLookupService documentLookupService,
            WorkspaceLookupService workspaceLookupService,
            BoardLookupService boardLookupService,
            BoardPermissionService boardPermissionService
    ) {
        this.documentRepository = documentRepository;
        this.documentLookupService = documentLookupService;
        this.workspaceLookupService = workspaceLookupService;
        this.boardLookupService = boardLookupService;
        this.boardPermissionService = boardPermissionService;
    }

    @Transactional
    public DocumentResponseDTO create(
            UUID workspaceId,
            UUID boardId,
            DocumentCreateDTO dto,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);

        WorkspaceModel workspace = workspaceLookupService.findActiveById(workspaceId);
        BoardModel board = boardLookupService.findInWorkspace(workspaceId, boardId);

        DocumentModel parent = resolveParent(workspaceId, boardId, dto.parentId());

        DocumentModel document = new DocumentModel();
        document.setTitle(dto.title().trim());
        document.setType(dto.type());
        document.setWorkspace(workspace);
        document.setBoard(board);
        document.setParent(parent);

        if (dto.type() == DocumentModel.Type.FILE) {
            document.setContent(dto.content());
        } else {
            document.setContent(null);
        }

        return DocumentMapper.toResponse(documentRepository.save(document));
    }

    private DocumentModel resolveParent(
            UUID workspaceId,
            UUID boardId,
            UUID parentId
    ) {
        if (parentId == null) {
            return null;
        }

        DocumentModel parent = documentLookupService.findInBoard(
                workspaceId,
                boardId,
                parentId
        );

        if (parent.getType() != DocumentModel.Type.FOLDER) {
            throw new InvalidDocumentOperationException("Parent must be a folder");
        }

        return parent;
    }
}