package home.projectmanager.dto;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName
) {
}
