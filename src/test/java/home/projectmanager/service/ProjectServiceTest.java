package home.projectmanager.service;

import home.projectmanager.dto.ProjectDto;
import home.projectmanager.entity.Project;
import home.projectmanager.entity.Team;
import home.projectmanager.entity.User;
import home.projectmanager.exception.project.*;
import home.projectmanager.exception.team.TeamAlreadyExistsException;
import home.projectmanager.repository.ProjectRepository;
import home.projectmanager.repository.TeamRepository;
import home.projectmanager.service.accesscontrol.AccessDecisionVoter;
import home.projectmanager.service.accesscontrol.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private AccessDecisionVoter accessDecisionVoter;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Team team;
    private Project project;
    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        team = new Team();
        team.setId(1L);
        team.setTeamName("Test Team");

        project = Project.builder()
                .id(1L)
                .projectName("Test Project")
                .projectDescription("Test Description")
                .teams(new ArrayList<>())
                .build();
    }

    @Test
    void createProject_ShouldCreateProject_WhenValidRequestIsProvided() {
        ProjectDto projectDto = ProjectDto.builder()
                .projectName("Test Project")
                .projectDescription("Test Description")
                .build();

        ProjectDto expectedProjectDto = ProjectDto.builder()
                .id(1L)
                .projectName("Test Project")
                .projectDescription("Test Description")
                .build();

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(accessDecisionVoter.hasPermission(team)).thenReturn(true);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDto result = projectService.createProject(projectDto, 1L);

        assertEquals(expectedProjectDto, result);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void createProject_ShouldThrowException_WhenProjectNameIsMissing() {
        ProjectDto projectDto = ProjectDto.builder()
                .projectName("")
                .projectDescription("New Description")
                .build();

        assertThrows(ProjectNameNotProvidedException.class, () -> projectService.createProject(projectDto, 1L));
    }

    @Test
    void createProject_ShouldThrowException_WhenProjectAlreadyExists() {
        ProjectDto projectDto = ProjectDto.builder()
                .projectName("Test Project")
                .projectDescription("Test Description")
                .build();

        when(projectRepository.findByProjectName("Test Project")).thenReturn(Optional.of(project));

        assertThrows(ProjectAlreadyExistsException.class, () -> projectService.createProject(projectDto, 1L));
    }

    @Test
    void getProject_ShouldReturnProject_WhenProjectExists() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(accessDecisionVoter.hasPermission(project)).thenReturn(true);

        ProjectDto expectedProjectDto = ProjectDto.builder()
                .id(1L)
                .projectName("Test Project")
                .projectDescription("Test Description")
                .build();

        ProjectDto result = projectService.getProject(1L);

        assertEquals(expectedProjectDto, result);
    }

    @Test
    void getProject_ShouldThrowException_WhenProjectDoesNotExist() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProject(1L));
    }

    @Test
    void deleteProject_ShouldDeleteProject_WhenProjectExists() {
        when(projectRepository.existsById(1L)).thenReturn(true);

        projectService.deleteProject(1L);

        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProject_ShouldThrowException_WhenProjectDoesNotExist() {
        when(projectRepository.existsById(1L)).thenReturn(false);

        assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(1L));
    }

    @Test
    void updateProject_ShouldUpdateProject_WhenValidRequestIsProvided() {
        ProjectDto projectDto = ProjectDto.builder()
                .projectName("Updated Project")
                .projectDescription("Updated Description")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(accessDecisionVoter.hasPermission(project)).thenReturn(true);
        when(projectRepository.save(project)).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectDto result = projectService.updateProject(1L, projectDto);

        assertEquals("Updated Project", project.getProjectName());
        assertEquals("Updated Description", project.getProjectDescription());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void addTeamToProject_ShouldAddTeam_WhenValidRequestIsProvided() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(accessDecisionVoter.hasPermission(project)).thenReturn(true);
        when(accessDecisionVoter.hasPermission(team)).thenReturn(true);

        projectService.addTeamToProject(1L, 1L);

        assertTrue(project.getTeams().contains(team));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void addTeamToProject_ShouldThrowException_WhenTeamAlreadyExists() {
        project.addTeam(team);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(accessDecisionVoter.hasPermission(project)).thenReturn(true);
        when(accessDecisionVoter.hasPermission(team)).thenReturn(true);

        assertThrows(TeamAlreadyExistsException.class, () -> projectService.addTeamToProject(1L, 1L));
    }

    @Test
    void getProjectsByUserId_ShouldReturnProjects_WhenValidUser() {
        when(authenticationFacade.getCurrentUser()).thenReturn(user);
        when(projectRepository.findAllByUserId(1L)).thenReturn(List.of(project));

        ProjectDto expectedProjectDto = ProjectDto.builder()
                .id(1L)
                .projectName("Test Project")
                .projectDescription("Test Description")
                .build();

        List<ProjectDto> result = projectService.getProjectsByUserId(1L);

        assertEquals(List.of(expectedProjectDto), result);
    }

    @Test
    void getProjects_ShouldReturnAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        ProjectDto expectedProjectDto = ProjectDto.builder()
                .id(1L)
                .projectName("Test Project")
                .projectDescription("Test Description")
                .build();

        List<ProjectDto> result = projectService.getProjects();

        assertEquals(List.of(expectedProjectDto), result);
    }
}
