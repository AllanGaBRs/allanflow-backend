package com.allan.task.manager.document;

import com.allan.task.manager.board.BoardLookupService;
import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.board.BoardPermissionService;
import com.allan.task.manager.document.dto.DocumentCreateDTO;
import com.allan.task.manager.document.dto.DocumentResponseDTO;
import com.allan.task.manager.document.dto.DocumentTreeDTO;
import com.allan.task.manager.document.dto.DocumentUpdateDTO;
import com.allan.task.manager.document.exception.InvalidDocumentOperationException;
import com.allan.task.manager.document.mapper.DocumentMapper;
import com.allan.task.manager.workspace.WorkspaceLookupService;
import com.allan.task.manager.workspace.WorkspaceModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public List<DocumentTreeDTO> findTree(
            UUID workspaceId,
            UUID boardId,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        boardLookupService.findInWorkspace(workspaceId, boardId);

        List<DocumentModel> documents =
                documentRepository.findByWorkspaceIdAndBoardIdOrderByTitleAsc(
                        workspaceId,
                        boardId
                );

        Map<UUID, List<DocumentModel>> documentsByParentId = documents.stream()
                .filter(document -> document.getParent() != null)
                .collect(Collectors.groupingBy(document -> document.getParent().getId()));

        return documents.stream()
                .filter(document -> document.getParent() == null)
                .map(document -> toTree(document, documentsByParentId))
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentResponseDTO findById(
            UUID workspaceId,
            UUID boardId,
            UUID documentId,
            UUID requesterId
    ){
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        boardLookupService.findInWorkspace(workspaceId, boardId);

        DocumentModel document = documentLookupService.findInBoard(
                workspaceId,
                boardId,
                documentId
        );

        return DocumentMapper.toResponse(document);
    }

    @Transactional
    public DocumentResponseDTO update(
            UUID workspaceId,
            UUID boardId,
            UUID documentId,
            DocumentUpdateDTO dto,
            UUID requesterId
    ) {
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        boardLookupService.findInWorkspace(workspaceId, boardId);

        DocumentModel document = documentLookupService.findInBoard(
                workspaceId,
                boardId,
                documentId
        );

        document.setTitle(dto.title().trim());

        if (document.getType() == DocumentModel.Type.FILE) {
            document.setContent(dto.content());
        } else {
            document.setContent(null);
        }

        return DocumentMapper.toResponse(documentRepository.save(document));
    }

    @Transactional
    public void delete(
            UUID workspaceId,
            UUID boardId,
            UUID documentId,
            UUID requesterId
    ){
        boardPermissionService.requireBoardAccess(workspaceId, boardId, requesterId);
        boardLookupService.findInWorkspace(workspaceId, boardId);

        DocumentModel document = documentLookupService.findInBoard(workspaceId, boardId, documentId);

        if (documentRepository.existsByParentId(documentId)) {
            throw new InvalidDocumentOperationException(
                    "Cannot delete a folder with children"
            );
        }

        documentRepository.delete(document);
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

    private DocumentTreeDTO toTree(
            DocumentModel document,
            Map<UUID, List<DocumentModel>> documentsByParentId
    ) {
        List<DocumentTreeDTO> children = documentsByParentId
                .getOrDefault(document.getId(), List.of())
                .stream()
                .map(child -> toTree(child, documentsByParentId))
                .toList();

        return DocumentMapper.toTree(document, children);
    }
}