package home.projectmanager.dto;

import home.projectmanager.entity.WorkItemStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record WorkItemDto(
        Long id,
        String title,
        String description,
        Integer points,
        WorkItemStatus status,
        Long parentWorkItemId,
        Long boardId,
        List<WorkItemDto> subWorkItems,
        List<WorkItemCommentDto> comments,
        UserDto assignedUser,
        BugItemDto bugItemDto
)
{
}