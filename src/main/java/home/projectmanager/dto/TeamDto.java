package home.projectmanager.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record TeamDto(
        Long id,
        String teamName,
        List<UserDto> users,
        List<ProjectDto> projects) {
}
