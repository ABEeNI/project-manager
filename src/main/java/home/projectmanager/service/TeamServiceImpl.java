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
import home.projectmanager.service.accesscontrol.AccessDecisionVoter;
import home.projectmanager.service.accesscontrol.AuthenticationFacade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {

    public final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final AccessDecisionVoter accessDecisionVoter;

    @Override
    public TeamDto createTeam(TeamDto teamDto) {
        if(teamDto.teamName().isBlank()) {
            throw new TeamNameNotProvidedException("Team name is not provided");
        }
        if(teamRepository.findByTeamName(teamDto.teamName()).isPresent()) {
            throw new TeamAlreadyExistsException("Team with name " + teamDto.teamName() + " already exists");
        }

        User currentUser = authenticationFacade.getCurrentUser();

        Team newTeam = new Team(teamDto.teamName());
        newTeam.addUser(currentUser);

        Team savedTeam = teamRepository.save(newTeam);
        log.info("Team with id {} created", savedTeam.getId());

        return convertToDto(savedTeam);
    }

    @Override
    public TeamDto getTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team with id " + id + " not found"));
        if(!accessDecisionVoter.hasPermission(team)) {
            throw new AccessDeniedException("User does not have permission to access team with id " + id);
        }
        List<User> users = team.getUsers();
        List<UserDto> userDtos = users.stream()
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email("")//email is not included, so email addresses are not exposed, but ""
                        .build())
                .collect(Collectors.toList());

        List<Project> projects = team.getProjects();
        List<ProjectDto> projectDtos = projects.stream()
                .map(project -> ProjectDto.builder()
                        .id(project.getId())
                        .projectName(project.getProjectName())
                        .projectDescription(project.getProjectDescription())
                        .build())
                .collect(Collectors.toList());

        log.info("Team with id {} found", id);
        return TeamDto.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .projects(projectDtos)
                .users(userDtos)
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
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team with id " + id + " not found"));
        if(!accessDecisionVoter.hasPermission(team)) {
            throw new AccessDeniedException("User does not have permission to delete team with id " + id);
        }
        teamRepository.deleteById(id); //Could change to teamRepository.delete(team) to avoid the deleteById call, however works the same
        log.info("Team with id {} deleted", id);
    }

    @Override
    public TeamDto updateTeam(Long id, TeamDto teamDto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException("Team with id " + id + " not found"));

        if(!accessDecisionVoter.hasPermission(team)) {
            throw new AccessDeniedException("User does not have permission to update team with id " + id);
        }
        team.setTeamName(teamDto.teamName());
        Team updatedTeam = teamRepository.save(team);
        log.info("Team with id {} updated", id);
        return convertToDto(updatedTeam);
    }

    @Override
    @Transactional
    public void addUserToTeam(Long teamId, String userEmail) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team with id " + teamId + " not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userEmail + " not found"));

        if(!accessDecisionVoter.hasPermission(team)) {
            throw new AccessDeniedException("User does not have permission to add user with useremail " + userEmail + " to team with id " + teamId);
        }

        if(team.getUsers().contains(user)) {
            throw new TeamAlreadyExistsException("User with useremail " + userEmail + " already exists in team with id " + teamId);
        }

        team.addUser(user);

        teamRepository.save(team);
        log.info("User with useremail {} added to team with id {}", userEmail, teamId);
    }

    @Override
    public void removeUserFromTeam(Long teamId, String userEmail) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team with id " + teamId + " not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userEmail + " not found"));

        if(!accessDecisionVoter.hasPermission(team)) {
            throw new AccessDeniedException("User does not have permission to remove user with useremail " + userEmail + " from team with id " + teamId);
        }
        if(!team.getUsers().contains(user)) {
            throw new TeamNotFoundException("User with useremail " + userEmail + " not found in team with id " + teamId);
        }

        team.removeUser(user);

        teamRepository.save(team);
        log.info("User with useremail {} removed from team with id {}", userEmail, teamId);
    }

    @Override
    public List<TeamDto> getTeamsByUserId(Long userId) {//could be adjusted, would not need the id, just for the currentUser from AuthenticationFacade
        List<Team> teams = teamRepository.findAllByUsersId(userId);
        return teams.stream().map(this::convertToDto).toList();
    }

    private TeamDto convertToDto(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .build();
    }
}
