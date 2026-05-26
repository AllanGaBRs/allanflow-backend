package com.allan.task.manager.workspace;

import org.springframework.stereotype.Service;

import java.text.Normalizer;

@Service
public class WorkspaceSlugService {

    private static final String FALLBACK_SLUG = "workspace";

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceSlugService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public String generateUniqueSlug(String name) {
        String baseSlug = slugify(name);
        String slug = baseSlug;
        int counter = 1;

        while (workspaceRepository.existsBySlugAndIsActiveTrue(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    private String slugify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        String slug = normalized
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        if (slug.isBlank()) {
            return FALLBACK_SLUG;
        }

        return slug;
    }
}
