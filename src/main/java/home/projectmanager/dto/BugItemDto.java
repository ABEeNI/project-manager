package home.projectmanager.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record BugItemDto(
        Long id,
        String title,
        String description,
        String status,
        List<WorkItemCommentDto> comments,
        Long workItemId
) {
}
