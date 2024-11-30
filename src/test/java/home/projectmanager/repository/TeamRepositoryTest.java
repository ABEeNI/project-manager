package home.projectmanager.repository;

import home.projectmanager.entity.Team;
import home.projectmanager.entity.User;
import home.projectmanager.entity.Role;
import lombok.extern.slf4j.Slf4j;
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
class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Team team;
    private Team team2;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .firstName("Bob")
                .lastName("Johnson")
                .email("bob.johnson@email.com")
                .password("password")
                .role(Role.USER)
                .teams(new ArrayList<>())
                .build();
        userRepository.save(user);


        team = Team.builder()
                .teamName("Gamma Team")
                .users(new ArrayList<>())
                .build();
        team.addUser(user);
        teamRepository.save(team);

        team2 = Team.builder()
                .teamName("Delta Team")
                .users(new ArrayList<>())
                .build();
        team2.addUser(user);
        teamRepository.save(team2);
    }

    @Test
    void testFindByTeamName() {
        Optional<Team> found = teamRepository.findByTeamName("Gamma Team");

        assertTrue(found.isPresent());
        assertEquals("Gamma Team", found.get().getTeamName());
    }

    @Test
    void testFindAllByUsersId() {
        List<Team> teams = teamRepository.findAllByUsersId(user.getId());

        assertEquals(2, teams.size());
        assertEquals(teams, List.of(team2, team));
    }
}