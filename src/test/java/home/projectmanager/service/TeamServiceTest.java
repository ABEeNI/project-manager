package home.projectmanager.service;

import home.projectmanager.dto.TeamDto;
import home.projectmanager.entity.Team;
import home.projectmanager.exception.TeamAlreadyExistsException;
import home.projectmanager.exception.TeamNameNotProvidedException;
import home.projectmanager.exception.TeamNotFoundException;
import home.projectmanager.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @InjectMocks
    private TeamServiceImpl teamService;

    @Mock
    private TeamRepository teamRepository;

    @Test
    void testCreateTeam() {
        TeamDto teamDto = TeamDto.builder()
                .teamName("Backend")
                .build();

        Team team = Team.builder()
                .teamName("Backend")
                .build();

        when(teamRepository.save(any(Team.class))).thenReturn(team);

        TeamDto createdTeamDto = teamService.createTeam(teamDto);

        assertEquals(teamDto, createdTeamDto);
    }

    @Test
    void testCreateTeamWithEmptyName() {
        TeamDto teamDto = TeamDto.builder()
                .teamName("")
                .build();

        assertThrows(TeamNameNotProvidedException.class, () -> teamService.createTeam(teamDto));
    }

    @Test
    void testCreateTeamWhenTeamAlreadyExists() {
        TeamDto teamDto = TeamDto.builder()
                .teamName("Backend")
                .build();

        Team team = Team.builder()
                .teamName("Backend")
                .build();

        when(teamRepository.findByTeamName("Backend")).thenReturn(Optional.of(team));

        assertThrows(TeamAlreadyExistsException.class, () -> teamService.createTeam(teamDto));
    }

    @Test
    void testGetTeamWhenIdGiven() {
        long id = 1L;
        String teamName = "Backend";
        Team team = Team.builder()
                .id(id)
                .teamName(teamName)
                .build();

        when(teamRepository.findById(id)).thenReturn(Optional.of(team));

        TeamDto teamDto = teamService.getTeam(id);

        assertEquals(team.getId(), teamDto.id());
        assertEquals(team.getTeamName(), teamDto.teamName());
    }

    @Test
    void testGetTeamWhenIdIsNotFound() {
        long id = 1L;
        when(teamRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TeamNotFoundException.class, () -> teamService.getTeam(id));
    }
}