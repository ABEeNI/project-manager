package home.projectmanager.dto;

public record BoardDto(
        Long id,
        String boardName,
        Long projectId
) {}
