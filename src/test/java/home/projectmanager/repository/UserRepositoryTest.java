package home.projectmanager.repository;

import home.projectmanager.entity.User;
import home.projectmanager.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByUsersId_ShouldReturnTeams_WhenUserIdExists() {
        User user = User.builder()
                .firstName("Carol")
                .lastName("Williams")
                .email("carol.williams@email.com")
                .password("password")
                .role(Role.USER)
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("carol.williams@email.com");

        assertTrue(found.isPresent(), "User should be found");
        assertEquals("carol.williams@email.com", found.get().getEmail());
        assertEquals("Carol", found.get().getFirstName());
    }
}
