package com.allan.task.manager.client.mapper;
import com.allan.task.manager.client.ClientModel;
import com.allan.task.manager.client.dto.ClientResponseDTO;

import java.util.List;

public class ClientMapper {

    public static ClientResponseDTO toResponse(ClientModel client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getCompany()
        );
    }

    public static List<ClientResponseDTO> toResponseList(List<ClientModel> clients) {
        return clients.stream()
                .map(ClientMapper::toResponse)
                .toList();
    }
}