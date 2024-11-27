package home.projectmanager.dto;

import lombok.Builder;


@Builder
public record ProjectDto(
        Long id,
        String projectName,
        String projectDescription) {
}
