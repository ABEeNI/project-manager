package home.projectmanager.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import home.projectmanager.controller.auth.AuthenticationRequest;
import home.projectmanager.controller.auth.AuthenticationResponse;
import home.projectmanager.controller.auth.RegistrationRequest;
import home.projectmanager.entity.*;
import home.projectmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegistrationRequest registrationRequest;
    private AuthenticationRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        registrationRequest = RegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

        loginRequest = AuthenticationRequest.builder()
                .email("john.doe@example.com")
                .password("password123")
                .build();
    }

    @Test
    void register_ShouldReturnAuthenticationResponse_WhenValidRequest() {
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("mockJwtToken");

        AuthenticationResponse response = authService.register(registrationRequest);

        assertNotNull(response, "Response should not be null");
        assertEquals(1L, response.getUserId(), "User ID should be 1");
        assertEquals("john.doe@example.com", response.getEmail(), "Email should match");
        assertEquals("mockJwtToken", response.getToken(), "JWT token should match");

        verify(passwordEncoder, times(1)).encode(registrationRequest.getPassword());
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    @Test
    void login_ShouldReturnAuthenticationResponse_WhenValidRequest() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("mockJwtToken");

        AuthenticationResponse response = authService.login(loginRequest);

        assertNotNull(response, "Response should not be null");
        assertEquals(1L, response.getUserId(), "User ID should match");
        assertEquals("john.doe@example.com", response.getEmail(), "Email should match");
        assertEquals("mockJwtToken", response.getToken(), "JWT token should match");

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtService, times(1)).generateToken(any(User.class));
    }
}