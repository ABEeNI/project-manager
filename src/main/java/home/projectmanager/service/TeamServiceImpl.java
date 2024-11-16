package home.projectmanager.service;

import home.projectmanager.dto.TeamDto;
import home.projectmanager.entity.Team;
import home.projectmanager.exception.TeamAlreadyExistsException;
import home.projectmanager.exception.TeamNameNotProvidedException;
import home.projectmanager.exception.TeamNotFoundException;
import home.projectmanager.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {

    public final TeamRepository teamRepository;

    @Override
    public TeamDto createTeam(TeamDto teamDto) {
        if(teamDto.teamName().isBlank()) {
            throw new TeamNameNotProvidedException("Team name is not provided");
        }
        if(teamRepository.findByTeamName(teamDto.teamName()).isPresent()) {
            throw  new TeamAlreadyExistsException("Team with name " + teamDto.teamName() + " already exists");
        }
        Team newTicket = Team.builder()
                .teamName(teamDto.teamName())
                .build();

        Team savedTeam = teamRepository.save(newTicket);
        log.info("Team with id {} created", savedTeam.getId());

        return convertToDto(savedTeam);
    }

    @Override
    public TeamDto getTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team with id " + id + " not found"));
        log.info("Team with id {} found", id);
        return TeamDto.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .build();
    }

    @Override
    public List<TeamDto> getTeams() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public void deleteTeam(Long id) {
        if(!teamRepository.existsById(id)) {
            throw new TeamNotFoundException("Team with id " + id + " not found");
        }
        teamRepository.deleteById(id);
        log.info("Team with id {} deleted", id);
    }

    @Override
    public TeamDto updateTeam(Long id, TeamDto teamDto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team with id " + id + " not found"));
        team.setTeamName(teamDto.teamName());
        Team updatedTeam = teamRepository.save(team);
        log.info("Team with id {} updated", id);
        return convertToDto(updatedTeam);
    }

    private TeamDto convertToDto(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .build();
    }
}
