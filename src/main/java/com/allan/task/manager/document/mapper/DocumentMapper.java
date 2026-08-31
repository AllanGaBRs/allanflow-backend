package com.allan.task.manager.document.mapper;

import com.allan.task.manager.document.DocumentModel;
import com.allan.task.manager.document.dto.DocumentResponseDTO;
import com.allan.task.manager.document.dto.DocumentTreeDTO;

import java.util.List;

public class DocumentMapper {

    private DocumentMapper(){

    }

    public static DocumentResponseDTO toResponse(DocumentModel document){
        return new DocumentResponseDTO(
                document.getId(),
                document.getTitle(),
                document.getType(),
                document.getParent() != null ? document.getParent().getId() : null,
                document.getContent()
        );
    }

    public static DocumentTreeDTO toTree(DocumentModel document, List<DocumentTreeDTO> children){
        return new DocumentTreeDTO(
                document.getId(),
                document.getTitle(),
                document.getType(),
                document.getParent() != null ? document.getParent().getId() : null,
                children
        );
    }

}
