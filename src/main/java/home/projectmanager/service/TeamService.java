package home.projectmanager.service;


import home.projectmanager.dto.TeamDto;


public interface TeamService {
    TeamDto createTeam(TeamDto teamDto);

    TeamDto getTeam(Long id);
}
