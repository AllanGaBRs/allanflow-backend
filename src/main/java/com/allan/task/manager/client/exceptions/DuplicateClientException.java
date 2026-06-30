package com.allan.task.manager.client.exceptions;

import com.allan.task.manager.client.dto.ClientResponseDTO;

import java.util.List;

public class DuplicateClientException extends RuntimeException {

    private final List<ClientResponseDTO> duplicates;

    public DuplicateClientException(List<ClientResponseDTO> duplicates) {
        super("Possible duplicate clients found.");
        this.duplicates = duplicates;
    }

    public List<ClientResponseDTO> getDuplicates() {
        return duplicates;
    }
}