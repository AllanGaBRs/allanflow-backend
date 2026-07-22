package com.allan.task.manager.workspace;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceSlugServiceTests {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @InjectMocks
    private WorkspaceSlugService workspaceSlugService;

    @Test
    void generateUniqueSlugShouldCreateSlugFromWorkspaceName() {
        when(workspaceRepository.existsBySlugAndIsActiveTrue("my-workspace"))
                .thenReturn(false);

        String result = workspaceSlugService.generateUniqueSlug("My Workspace");

        assertEquals("my-workspace", result);
    }

    @Test
    void generateUniqueSlugShouldRemoveAccents() {
        when(workspaceRepository.existsBySlugAndIsActiveTrue("gestao-de-projetos"))
                .thenReturn(false);

        String result = workspaceSlugService.generateUniqueSlug("Gestão de Projetos");

        assertEquals("gestao-de-projetos", result);
    }

    @Test
    void generateUniqueSlugShouldRemoveSpecialCharacters() {
        when(workspaceRepository.existsBySlugAndIsActiveTrue("workspace-2026"))
                .thenReturn(false);

        String result = workspaceSlugService.generateUniqueSlug("Workspace @#$ 2026!");

        assertEquals("workspace-2026", result);
    }

    @Test
    void generateUniqueSlugShouldUseFallbackWhenNameDoesNotGenerateValidSlug() {
        when(workspaceRepository.existsBySlugAndIsActiveTrue("workspace"))
                .thenReturn(false);

        String result = workspaceSlugService.generateUniqueSlug("@#$%");

        assertEquals("workspace", result);
    }

    @Test
    void generateUniqueSlugShouldAppendCounterWhenSlugAlreadyExists() {
        when(workspaceRepository.existsBySlugAndIsActiveTrue("my-workspace"))
                .thenReturn(true);

        when(workspaceRepository.existsBySlugAndIsActiveTrue("my-workspace-1"))
                .thenReturn(false);

        String result = workspaceSlugService.generateUniqueSlug("My Workspace");

        assertEquals("my-workspace-1", result);
    }

    @Test
    void generateUniqueSlugShouldIncrementCounterUntilAvailableSlugIsFound() {
        when(workspaceRepository.existsBySlugAndIsActiveTrue("my-workspace"))
                .thenReturn(true);

        when(workspaceRepository.existsBySlugAndIsActiveTrue("my-workspace-1"))
                .thenReturn(true);

        when(workspaceRepository.existsBySlugAndIsActiveTrue("my-workspace-2"))
                .thenReturn(false);

        String result = workspaceSlugService.generateUniqueSlug("My Workspace");

        assertEquals("my-workspace-2", result);
    }
}