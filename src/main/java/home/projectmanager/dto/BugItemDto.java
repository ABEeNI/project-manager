package home.projectmanager.dto;

import home.projectmanager.entity.BugItemStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record BugItemDto(
        Long id,
        String title,
        String description,
        BugItemStatus status,
        List<BugItemCommentDto> comments,
        Long projectId,
        WorkItemDto workItemDto,
        UserDto reporter
) {
}
