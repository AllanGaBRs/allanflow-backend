package com.allan.task.manager.label;

import com.allan.task.manager.label.exception.LabelNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LabelLookupService {

    private final LabelRepository labelRepository;

    public LabelLookupService(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    public LabelModel findByIdAndBoard(UUID boardId, UUID labelId) {
        return labelRepository.findByIdAndBoardId(labelId, boardId)
                .orElseThrow(() -> new LabelNotFoundException("Label not found"));
    }

    public LabelModel findInBoard(UUID boardId, UUID labelId) {
        return findByIdAndBoard(boardId, labelId);
    }
}