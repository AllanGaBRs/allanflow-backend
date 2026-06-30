package com.allan.task.manager.client;

import com.allan.task.manager.client.dto.ClientCreateDTO;
import com.allan.task.manager.client.dto.ClientResponseDTO;
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
    private final WorkspacePermissionService workspacePermissionService;
    private final WorkspaceLookupService workspaceLookupService;

    public ClientService(ClientRepository clientRepository, WorkspacePermissionService workspacePermissionService, WorkspaceLookupService workspaceLookupService) {
        this.clientRepository = clientRepository;
        this.workspacePermissionService = workspacePermissionService;
        this.workspaceLookupService = workspaceLookupService;
    }

    @Transactional
    public ClientResponseDTO create(
            UUID workspaceId,
            ClientCreateDTO dto,
            UUID requesterId
    ){
        workspacePermissionService.requireMember(workspaceId, requesterId);

        WorkspaceModel workspace = workspaceLookupService.findActiveById(workspaceId);

        if (!dto.forceCreate()) {

            List<ClientModel> duplicates = clientRepository.findPossibleDuplicates(
                    workspace,
                    dto.name(),
                    dto.email(),
                    dto.phone()
            );

            if (!duplicates.isEmpty()) {
                throw new DuplicateClientException(
                        ClientMapper.toResponseList(duplicates)
                );
            }
        }
        ClientModel client = new ClientModel();

        client.setName(dto.name());
        client.setEmail(dto.email());
        client.setPhone(dto.phone());
        client.setCompany(dto.company());
        client.setWorkspace(workspace);

        client = clientRepository.save(client);

        return ClientMapper.toResponse(client);
   }
}
