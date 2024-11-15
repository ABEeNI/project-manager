package home.projectmanager.dto;

import lombok.Builder;

@Builder
public record TeamDto(Long id, String teamName) {

}
