package com.allan.task.manager.comment.mapper;

import com.allan.task.manager.comment.CommentModel;
import com.allan.task.manager.comment.dto.CommentResponseDTO;

import java.util.List;

public class CommentMapper {

    private CommentMapper() {
    }

    public static CommentResponseDTO toResponse(CommentModel comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getTask().getId(),
                comment.getCreatedAt()
        );
    }

    public static List<CommentResponseDTO> toResponseList(
            List<CommentModel> comments
    ) {
        return comments.stream()
                .map(CommentMapper::toResponse)
                .toList();
    }
}