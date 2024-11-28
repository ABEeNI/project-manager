package home.projectmanager.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record BoardDto(
        Long id,
        String boardName,
        Long projectId,
        List<WorkItemDto> workItemDtos
) {}
