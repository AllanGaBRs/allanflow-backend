package com.allan.task.manager.client;

import com.allan.task.manager.client.dto.ClientCreateDTO;
import com.allan.task.manager.client.dto.ClientResponseDTO;
import com.allan.task.manager.client.dto.ClientUpdateDTO;
import com.allan.task.manager.client.exceptions.DuplicateClientException;
import com.allan.task.manager.client.mapper.ClientMapper;
import com.allan.task.manager.workspace.WorkspaceLookupService;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.WorkspacePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientLookupService clientLookupService;
    private final WorkspacePermissionService workspacePermissionService;
    private final WorkspaceLookupService workspaceLookupService;

    public ClientService(
            ClientRepository clientRepository,
            ClientLookupService clientLookupService,
            WorkspacePermissionService workspacePermissionService,
            WorkspaceLookupService workspaceLookupService
    ) {
        this.clientRepository = clientRepository;
        this.clientLookupService = clientLookupService;
        this.workspacePermissionService = workspacePermissionService;
        this.workspaceLookupService = workspaceLookupService;
    }

    @Transactional
    public ClientResponseDTO create(
            UUID workspaceId,
            ClientCreateDTO dto,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        WorkspaceModel workspace =
                workspaceLookupService.findActiveById(workspaceId);

        String name = dto.name().trim();
        String email = normalize(dto.email());
        String phone = normalize(dto.phone());
        String company = normalize(dto.company());

        if (!dto.forceCreate()) {
            List<ClientModel> duplicates =
                    clientRepository.findPossibleDuplicates(
                            workspace,
                            name,
                            email,
                            phone
                    );

            if (!duplicates.isEmpty()) {
                throw new DuplicateClientException(
                        ClientMapper.toResponseList(duplicates)
                );
            }
        }

        ClientModel client = new ClientModel();

        client.setName(name);
        client.setEmail(email);
        client.setPhone(phone);
        client.setCompany(company);
        client.setWorkspace(workspace);

        client = clientRepository.save(client);

        return ClientMapper.toResponse(client);
    }

    @Transactional
    public ClientResponseDTO update(
            UUID workspaceId,
            UUID clientId,
            ClientUpdateDTO dto,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        WorkspaceModel workspace =
                workspaceLookupService.findActiveById(workspaceId);

        ClientModel client = clientLookupService.findInWorkspace(
                workspaceId,
                clientId
        );

        String name = dto.name().trim();
        String email = normalize(dto.email());
        String phone = normalize(dto.phone());
        String company = normalize(dto.company());

        if (!dto.forceUpdate()) {
            List<ClientModel> duplicates =
                    clientRepository.findPossibleDuplicatesExcludingClient(
                            workspace,
                            clientId,
                            name,
                            email,
                            phone
                    );

            if (!duplicates.isEmpty()) {
                throw new DuplicateClientException(
                        ClientMapper.toResponseList(duplicates)
                );
            }
        }

        client.setName(name);
        client.setEmail(email);
        client.setPhone(phone);
        client.setCompany(company);

        return ClientMapper.toResponse(client);
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO findById(
            UUID workspaceId,
            UUID clientId,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        workspaceLookupService.findActiveById(workspaceId);

        ClientModel client = clientLookupService.findInWorkspace(workspaceId, clientId);

        return ClientMapper.toResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponseDTO> findAll(
            UUID workspaceId,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        workspaceLookupService.findActiveById(workspaceId);

        return ClientMapper.toResponseList(
                clientLookupService.findAllInWorkspace(workspaceId)
        );
    }

    @Transactional
    public void delete(
            UUID workspaceId,
            UUID clientId,
            UUID requesterId
    ) {
        workspacePermissionService.requireMember(workspaceId, requesterId);

        workspaceLookupService.findActiveById(workspaceId);

        ClientModel client = clientLookupService.findInWorkspace(workspaceId, clientId);

        clientRepository.delete(client);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
