package home.projectmanager.service;

import home.projectmanager.dto.TeamDto;
import home.projectmanager.entity.Team;
import home.projectmanager.exception.TeamAlreadyExistsException;
import home.projectmanager.exception.TeamNameNotProvidedException;
import home.projectmanager.exception.TeamNotFoundException;
import home.projectmanager.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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

        return TeamDto.builder()
                .teamName(savedTeam.getTeamName())
                .build();
    }

    @Override
    public TeamDto getTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team with id " + id + " not found"));
        return TeamDto.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .build();
    }
}
