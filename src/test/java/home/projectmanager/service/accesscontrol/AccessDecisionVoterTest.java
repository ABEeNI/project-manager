package home.projectmanager.service.accesscontrol;

import home.projectmanager.entity.Board;
import home.projectmanager.entity.Project;
import home.projectmanager.entity.Team;
import home.projectmanager.entity.User;
import home.projectmanager.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessDecisionVoterTest {

    @InjectMocks
    AccessDecisionVoter accessDecisionVoter;

    @Mock
    AuthenticationFacade authenticationFacade;

    @Mock
    ProjectRepository projectRepository;

    User currentUser;

    @BeforeEach
    void setUp() {
        Long id = 1L;
        String email = "test@example.com";
        String firstname = "John";
        String lastName = "Doe";
        currentUser = new User();
        currentUser.setId(id);
        currentUser.setEmail(email);
        currentUser.setFirstName(firstname);
        currentUser.setLastName(lastName);

    }

    @Test
    void hasPermission_ShouldReturnTrue_WhenUserHasPermissionToProject() {
        Project project = new Project();
        project.setId(1L);
        project.setProjectName("Project");
        project.setProjectDescription("Description");

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.findAllByUserId(currentUser.getId())).thenReturn(List.of(project));

        assertTrue(accessDecisionVoter.hasPermission(project));
    }

    @Test
    void hasPermission_ShouldReturnFalse_WhenUserDoesNotHavePermissionToProject() {
        Project project = new Project();
        project.setId(1L);
        project.setProjectName("Project");
        project.setProjectDescription("Description");

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.findAllByUserId(currentUser.getId())).thenReturn(List.of());

        assertFalse(accessDecisionVoter.hasPermission(project));
    }

    @Test
    void hasPermission_ShouldReturnTrue_WhenUserHasPermissionToBoard() {
        Project project = new Project();
        project.setId(1L);
        project.setProjectName("Project");
        project.setProjectDescription("Description");

        Board board = new Board();
        board.setId(1L);
        board.setBoardName("Board");
        board.setProjectId(project.getId());

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.findAllByUserId(currentUser.getId())).thenReturn(List.of(project));

        assertTrue(accessDecisionVoter.hasPermission(board));
    }

    @Test
    void hasPermission_ShouldReturnFalse_WhenUserDoesNotHavePermissionToBoard() {
        Project project = new Project();
        project.setId(1L);
        project.setProjectName("Project");
        project.setProjectDescription("Description");

        Board board = new Board();
        board.setId(1L);
        board.setBoardName("Board");
        Long anotherProjectsId = 2L;
        board.setProjectId(anotherProjectsId);

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(projectRepository.findAllByUserId(currentUser.getId())).thenReturn(List.of(project));

        assertFalse(accessDecisionVoter.hasPermission(board));
    }

    @Test
    void hasPermission_ShouldReturnTrue_WhenUserHasPermissionToTeam() {
        Team team = new Team();
        team.setId(1L);
        team.setTeamName("Team");
        team.addUser(currentUser);

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);

        assertTrue(accessDecisionVoter.hasPermission(team));
    }

    @Test
    void hasPermission_ShouldReturnFalse_WhenUserDoesNotHavePermissionToTeam() {
        Team team = new Team();
        team.setId(1L);
        team.setTeamName("Team");

        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);

        assertFalse(accessDecisionVoter.hasPermission(team));
    }

}