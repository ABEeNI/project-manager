package home.projectmanager.service;

import home.projectmanager.dto.ProjectDto;
import home.projectmanager.dto.TeamDto;
import home.projectmanager.dto.UserDto;
import home.projectmanager.entity.Project;
import home.projectmanager.entity.Team;
import home.projectmanager.entity.User;
import home.projectmanager.exception.team.TeamAlreadyExistsException;
import home.projectmanager.exception.team.TeamNameNotProvidedException;
import home.projectmanager.exception.team.TeamNotFoundException;
import home.projectmanager.exception.user.UserNotFoundException;
import home.projectmanager.repository.TeamRepository;
import home.projectmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @InjectMocks
    private TeamServiceImpl teamService;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Test
    void testCreateTeam() { //needs refactoring after changes, but with integration test it currently works
        TeamDto teamDto = TeamDto.builder()
                .teamName("Backend")
                .build();

        Team team = Team.builder()
                .teamName("Backend")
                .build();

        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamRepository.findByTeamName("Backend")).thenReturn(Optional.empty());
        when(authenticationFacade.getCurrentUser()).thenReturn(User.builder().teams(new ArrayList<>()).build());//probably will need to change this

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
        Long id = 1L;
        String teamName = "Backend";
        Team team = Team.builder()
                .id(id)
                .teamName(teamName)
                .users(new ArrayList<>())
                .projects(new ArrayList<>())
                .build();
        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("test@email.com")
                .teams(new ArrayList<>())
                .build();
        team.addUser(user);
        Project project = Project.builder()
                .id(1L)
                .projectName("Project")
                .projectDescription("Description")
                .teams(new ArrayList<>())
                .boards(new ArrayList<>())
                //.bugItems(new ArrayList<>())
                .build();
        team.addProject(project);
        TeamDto expectedTeamDto = TeamDto.builder()
                .id(id)
                .teamName(teamName)
                .projects(List.of(ProjectDto.builder()
                        .id(1L)
                        .projectName("Project")
                        .projectDescription("Description")
                        .build()))
                .users(List.of(UserDto.builder()
                        .id(1L)
                        .firstName("John")
                        .lastName("Doe")
                        .email("")
                        .build()))
                .build();
        when(teamRepository.findById(id)).thenReturn(Optional.of(team));

        TeamDto result = teamService.getTeam(id);

        assertEquals(expectedTeamDto, result);
    }

    @Test
    void testGetTeamWhenIdIsNotFound() {
        long id = 1L;
        when(teamRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TeamNotFoundException.class, () -> teamService.getTeam(id));
    }

    @Test
    void testGetTeams() {
        Team team1 = Team.builder()
                .id(1L)
                .teamName("Backend")
                .build();

        Team team2 = Team.builder()
                .id(2L)
                .teamName("Frontend")
                .build();

        when(teamRepository.findAll()).thenReturn(List.of(team1, team2));

        TeamDto teamDto1 = TeamDto.builder()
                .id(1L)
                .teamName("Backend")
                .build();

        TeamDto teamDto2 = TeamDto.builder()
                .id(2L)
                .teamName("Frontend")
                .build();

        assertEquals(List.of(teamDto1, teamDto2), teamService.getTeams());
    }

    @Test
    void testDeleteTeamWhenIdIsValid() {
        long id = 1L;

        when(teamRepository.existsById(id)).thenReturn(true);

        teamService.deleteTeam(id);

        verify(teamRepository).deleteById(id);
    }

    @Test
    void testDeleteTeamWhenIdIsNotFound() {
        long id = 1L;

        when(teamRepository.existsById(id)).thenReturn(false);

        assertThrows(TeamNotFoundException.class, () -> teamService.deleteTeam(id));
    }

    @Test
    void testUpdateTeamWhenIdIsValid() {
        long id = 1L;
        String teamName = "Backend";
        TeamDto teamDto = TeamDto.builder()
                .teamName(teamName)
                .build();

        Team team = Team.builder()
                .id(id)
                .teamName(teamName)
                .build();

        when(teamRepository.findById(id)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(team);

        TeamDto updatedTeamDto = teamService.updateTeam(id, teamDto);

        assertEquals(teamDto.teamName(), updatedTeamDto.teamName());
    }

    @Test
    void testUpdateTeamWhenIdIsNotFound() {
        long id = 1L;
        String teamName = "Backend";
        TeamDto teamDto = TeamDto.builder()
                .teamName(teamName)
                .build();

        when(teamRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TeamNotFoundException.class, () -> teamService.updateTeam(id, teamDto));
    }

    @Test
    void testAddUserToTeam() {
        long teamId = 1L;
        String userEmail = "john.doe@eamil.com";

        User userToBeAdded = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email(userEmail)
                .teams(new ArrayList<>())
                .build();
        User user = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@email.com")
                .teams(new ArrayList<>())
                .build();

        Team team = Team.builder()
                .id(teamId)
                .teamName("Backend")
                .users(new ArrayList<>(List.of(user)))
                .build();

        user.getTeams().add(team);


        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userToBeAdded));

        teamService.addUserToTeam(teamId, userEmail);

        assertTrue(team.getUsers().contains(userToBeAdded));
        assertTrue(userToBeAdded.getTeams().contains(team));
        verify(teamRepository, times(1)).save(any(Team.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testAddUserToTeamWhenTeamIdIsNotFound() {
        long teamId = 1L;
        String userEmail = "john.doe@email.com";

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        assertThrows(TeamNotFoundException.class, () -> teamService.addUserToTeam(teamId, userEmail));
    }

    @Test
    void testAddUserToTeamWhenUserEmailIsNotFound() {
        long teamId = 1L;
        String userEmail = "john.doe@email.com";

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(Team.builder().build()));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> teamService.addUserToTeam(teamId, userEmail));
    }

    @Test
    void testAddUserToTeamWhenUserAlreadyExistsInTeam() {
        long teamId = 1L;
        String userEmail = "john.doe@eamil.com";

        User userToBeAdded = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email(userEmail)
                .teams(new ArrayList<>())
                .build();
        User user = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@email.com")
                .teams(new ArrayList<>())
                .build();

        Team team = Team.builder()
                .id(teamId)
                .teamName("Backend")
                .users(new ArrayList<>(List.of(user, userToBeAdded)))
                .build();

        user.getTeams().add(team);
        userToBeAdded.getTeams().add(team);


        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userToBeAdded));

        assertThrows(TeamAlreadyExistsException.class, () -> teamService.addUserToTeam(teamId, userEmail));
    }
    //TODO Test for removeUserFromTeam no user with email, no team with id, user not in team

    @Test
    void getTeamsByUserId_ShouldReturnTeams_WhenUserExists() {
        Long userId = 1L;

        List<TeamDto> expectedTeamDtos = List.of(
                TeamDto.builder()
                        .id(1L)
                        .teamName("Backend")
                        .build(),
                TeamDto.builder()
                        .id(2L)
                        .teamName("Frontend")
                        .build()
        );

        List<Team> teams = List.of(
                Team.builder()
                        .id(1L)
                        .teamName("Backend")
                        .users(new ArrayList<>())
                        .projects(new ArrayList<>())
                        .build(),
                Team.builder()
                        .id(2L)
                        .users(new ArrayList<>())
                        .projects(new ArrayList<>())
                        .teamName("Frontend")
                        .build()
        );

        when(teamRepository.findByUsersId(userId)).thenReturn(teams);

        List<TeamDto> result = teamService.getTeamsByUserId(userId);

        assertEquals(expectedTeamDtos, result);
    }

    @Test
    void getTeamsByUserId_ShouldReturnEmptyList_WhenUserDoesNotExist() {
        Long userId = 1L;

        when(teamRepository.findByUsersId(userId)).thenReturn(new ArrayList<>());

        List<TeamDto> result = teamService.getTeamsByUserId(userId);

        assertTrue(result.isEmpty());
    }
}