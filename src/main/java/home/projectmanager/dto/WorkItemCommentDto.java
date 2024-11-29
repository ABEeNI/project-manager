package home.projectmanager.dto;

import lombok.Builder;

@Builder
public record WorkItemCommentDto(
        Long id,
        String comment,
        String commenter
) {
}
