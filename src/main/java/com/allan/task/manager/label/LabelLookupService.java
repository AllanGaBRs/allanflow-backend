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

    public LabelModel findInBoard(UUID workspaceId, UUID boardId, UUID labelId) {
        return labelRepository.findByIdAndBoardIdAndBoardWorkspaceId(labelId, boardId, workspaceId)
                .orElseThrow(() -> new LabelNotFoundException("Label not found"));
    }

    public List<LabelModel> findAllInBoard(UUID workspaceId, UUID boardId, Set<UUID> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return List.of();
        }

        List<LabelModel> labels = labelRepository.findByBoardIdAndBoardWorkspaceIdAndIdIn(
                boardId, workspaceId, labelIds
        );

        if (labels.size() != labelIds.size()) {
            throw new LabelNotFoundException("One or more labels were not found");
        }

        return labels;
    }
}