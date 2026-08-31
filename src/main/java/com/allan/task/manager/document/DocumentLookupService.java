package com.allan.task.manager.document;

import com.allan.task.manager.document.exception.DocumentNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DocumentLookupService {

    private final DocumentRepository documentRepository;

    public DocumentLookupService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public DocumentModel findInBoard(UUID workspaceId, UUID boardId, UUID documentId){
        return documentRepository
                .findByIdAndWorkspaceIdAndBoardId(documentId, workspaceId, boardId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
    }

}
