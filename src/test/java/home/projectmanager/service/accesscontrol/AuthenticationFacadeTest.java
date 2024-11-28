package home.projectmanager.service.accesscontrol;

import home.projectmanager.entity.User;
import home.projectmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationFacadeTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationFacade authenticationFacade;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCurrentUser_ShouldReturnUser_WhenUserExists() {
        String email = "test@email.com";
        User user = new User();
        user.setEmail(email);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = authenticationFacade.getCurrentUser();

        assertEquals(user, result);
    }

    @Test
    void getCurrentUser_ShouldThrowException_WhenUserDoesNotExist() {
        String email = "test@email.com";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authenticationFacade.getCurrentUser());
    }
}