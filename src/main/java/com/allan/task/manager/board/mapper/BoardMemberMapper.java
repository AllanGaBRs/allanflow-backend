package com.allan.task.manager.board.mapper;

import com.allan.task.manager.board.dto.BoardMemberResponseDTO;
import com.allan.task.manager.user.UserModel;

import java.util.List;

public class BoardMemberMapper {

    private BoardMemberMapper() {
    }

    public static BoardMemberResponseDTO toResponse(UserModel user) {
        return new BoardMemberResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static List<BoardMemberResponseDTO> toResponseList(List<UserModel> users) {
        return users.stream()
                .map(BoardMemberMapper::toResponse)
                .toList();
    }
}