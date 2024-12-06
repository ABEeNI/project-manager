package home.projectmanager.repository;

import home.projectmanager.entity.Project;
import home.projectmanager.entity.Team;
import home.projectmanager.entity.User;
import home.projectmanager.entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Team team;
    private Project project;
    private Project project2;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice.smith@example.com")
                .password("password")
                .role(Role.USER)
                .teams(new ArrayList<>())
                .build();
        userRepository.save(user);

        team = Team.builder()
                .teamName("Team Alpha")
                .users(new ArrayList<>())
                .projects(new ArrayList<>())
                .build();
        team.addUser(user);
        teamRepository.save(team);

        project = Project.builder()
                .projectName("Alpha Project")
                .projectDescription("Project associated with Team Alpha")
                .teams(new ArrayList<>())
                .build();
        project.addTeam(team);
        projectRepository.save(project);

        project2 = Project.builder()
                .projectName("Beta Project")
                .projectDescription("Project associated with Team Alpha")
                .teams(new ArrayList<>())
                .build();
        project2.addTeam(team);
        projectRepository.save(project2);
    }

    @Test
    void createBoard_ShouldReturnCreatedBoard_WhenValidBoardDtoIsProvided() {
        Optional<Project> found = projectRepository.findByProjectName("Alpha Project");

        assertTrue(found.isPresent());
        assertEquals("Alpha Project", found.get().getProjectName());
    }

    @Test
    void findAllByUserId_ShouldReturnProjects_WhenUserIdExists() {
        List<Project> projects = projectRepository.findAllByUserId(user.getId());

        assertEquals(2, projects.size());
        assertEquals("Alpha Project", projects.get(0).getProjectName());
        assertEquals(List.of(project, project2), projects);
    }
}
