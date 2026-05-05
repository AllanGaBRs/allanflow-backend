package com.allan.task.manager.membership.mapper;

import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.membership.dto.MembershipResponseDTO;

import java.util.List;

public class MembershipMapper {

    public static MembershipResponseDTO toResponse(MembershipModel membership) {
        return new MembershipResponseDTO(
                membership.getId(),
                membership.getUser().getId(),
                membership.getUser().getName(),
                membership.getUser().getEmail(),
                membership.getRole()
        );
    }

    public static List<MembershipResponseDTO> toResponseList(List<MembershipModel> memberships) {
        return memberships.stream()
                .map(MembershipMapper::toResponse)
                .toList();
    }
}