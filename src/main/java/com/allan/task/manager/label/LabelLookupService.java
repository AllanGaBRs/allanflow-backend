package com.allan.task.manager.label;

import com.allan.task.manager.label.exception.LabelNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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

    public List<LabelModel> findAllInBoard(UUID boardId, Set<UUID> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return List.of();
        }

        List<LabelModel> labels =
                labelRepository.findByBoardIdAndIdIn(boardId, labelIds);

        if (labels.size() != labelIds.size()) {
            throw new LabelNotFoundException("One or more labels were not found");
        }

        return labels;
    }
}