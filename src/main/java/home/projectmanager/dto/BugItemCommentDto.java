package home.projectmanager.dto;

import lombok.Builder;

@Builder
public record BugItemCommentDto(
        Long id,
        String comment,
        UserDto commenter
) {
}
