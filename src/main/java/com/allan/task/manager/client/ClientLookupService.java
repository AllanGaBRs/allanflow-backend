package com.allan.task.manager.client;

import com.allan.task.manager.client.exceptions.ClientNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientLookupService {

    private final ClientRepository clientRepository;

    public ClientLookupService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public ClientModel findInWorkspace(UUID workspaceId, UUID clientId) {
        return clientRepository.findByIdAndWorkspaceId(clientId, workspaceId)
                .orElseThrow(() -> new ClientNotFoundException("Client not found"));
    }

    public List<ClientModel> findAllInWorkspace(UUID workspaceId) {
        return clientRepository.findAllByWorkspaceId(workspaceId);
    }
}