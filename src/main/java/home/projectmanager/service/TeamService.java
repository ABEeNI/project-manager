package home.projectmanager.service;


import home.projectmanager.dto.TeamDto;

import java.util.List;


public interface TeamService {
    TeamDto createTeam(TeamDto teamDto);

    TeamDto getTeam(Long id);

    List<TeamDto> getTeams();

    void deleteTeam(Long id);

    TeamDto updateTeam(Long id, TeamDto teamDto);
}
