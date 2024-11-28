package home.projectmanager.service;

import home.projectmanager.dto.ProjectDto;
import home.projectmanager.entity.Project;
import home.projectmanager.entity.Team;
import home.projectmanager.entity.User;
import home.projectmanager.exception.project.ProjectAlreadyExistsException;
import home.projectmanager.exception.project.ProjectNameNotProvidedException;
import home.projectmanager.exception.project.ProjectNotFoundException;
import home.projectmanager.exception.team.TeamAlreadyExistsException;
import home.projectmanager.exception.team.TeamNotFoundException;
import home.projectmanager.repository.ProjectRepository;
import home.projectmanager.repository.TeamRepository;
import home.projectmanager.service.accesscontrol.AccessDecisionVoter;
import home.projectmanager.service.accesscontrol.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final AuthenticationFacade authenticationFacade;
    private final AccessDecisionVoter accessDecisionVoter;

    @Override
    public ProjectDto createProject(ProjectDto projectDto, Long teamId) {
        if (projectDto.projectName().isBlank()) {
            throw new ProjectNameNotProvidedException("Project name is not provided");
        }

        if (projectRepository.findByProjectName(projectDto.projectName()).isPresent()) {
            throw new ProjectAlreadyExistsException("Project with name " + projectDto.projectName() + " already exists");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team with id " + teamId + " not found"));

        if(!accessDecisionVoter.hasPermission(team)) {
            throw new AccessDeniedException("User does not have permission to create project with team id " + teamId);
        }

        Project project = Project.builder()
                .projectName(projectDto.projectName())
                .projectDescription(projectDto.projectDescription())
                .teams(new ArrayList<>())
                .build();

        project.addTeam(team);

        Project savedProject = projectRepository.save(project);
        log.info("Project with id {} created and team with id {} is added to project", savedProject.getId(), teamId);
        return convertToDto(savedProject);
    }

    @Override
    public ProjectDto getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + id + " not found"));
        if(!accessDecisionVoter.hasPermission(project)) {
            throw new AccessDeniedException("User does not have permission to project with id " + id);
        }
        log.info("Project with id {} found", id);
        return convertToDto(project);
    }

    @Override
    public List<ProjectDto> getProjects() { //ADMIN ROLE needed
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProject(Long id) { //ADMIN ROLE needed
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Project with id " + id + " not found");
        }
        projectRepository.deleteById(id);
        log.info("Project with id {} deleted", id);
    }

    @Override
    public ProjectDto updateProject(Long projectId, ProjectDto projectDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + projectId + " not found"));

        if(!accessDecisionVoter.hasPermission(project)) {
            throw new AccessDeniedException("User does not have permission to project with id " + projectId);
        }

        if (projectDto.projectName() != null && !projectDto.projectName().isBlank()) {
            project.setProjectName(projectDto.projectName());
        }
        if (projectDto.projectDescription() != null) {
            project.setProjectDescription(projectDto.projectDescription());
        }

        Project updatedProject = projectRepository.save(project);
        log.info("Project with id {} updated", projectId);
        return convertToDto(updatedProject);
    }

    @Override
    public void addTeamToProject(Long projectId, Long teamId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + projectId + " not found"));

        if(!accessDecisionVoter.hasPermission(project)) {
            throw new AccessDeniedException("User does not have permission to project with id " + projectId);
        }
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team with id " + teamId + " not found"));
        if(!accessDecisionVoter.hasPermission(team)) {
            throw new AccessDeniedException("User does not have permission to team with id " + teamId);
        }
        if(project.getTeams().contains(team)) {
            throw new TeamAlreadyExistsException("Team with id " + teamId + " already exists in project with id " + projectId);
        }
        project.addTeam(team);

        projectRepository.save(project);
        log.info("Team with id {} added to project with id {}", teamId, projectId);
    }

    @Override
    public void removeTeamFromProject(Long projectId, Long teamId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + projectId + " not found"));

        if(!accessDecisionVoter.hasPermission(project)) {
            throw new AccessDeniedException("User does not have permission to project with id " + projectId);
        }
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team with id " + teamId + " not found"));
        if(!accessDecisionVoter.hasPermission(team)) {
            throw new AccessDeniedException("User does not have permission to team with id " + teamId);
        }
        if(project.getTeams().contains(team)) {
            throw new TeamAlreadyExistsException("Team with id " + teamId + " already exists in project with id " + projectId);
        }

        project.removeTeam(team);
        projectRepository.save(project);
        log.info("Team with id {} removed from project with id {}", teamId, projectId);
    }

    @Override
    public List<ProjectDto> getProjectsByUserId(Long userId) {
        User currentUser = authenticationFacade.getCurrentUser();
        if (!currentUser.getId().equals(userId)) { //might have to refactor these to not ask for userid just /user
            throw new AccessDeniedException("User with id " + userId + " is not the current user");
        }

        List<Project> projects = projectRepository.findAllByUserId(userId);
        return projects.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProjectDto convertToDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .projectDescription(project.getProjectDescription())
                .build();
    }
}