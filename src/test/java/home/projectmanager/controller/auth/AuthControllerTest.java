package home.projectmanager.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.projectmanager.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ShouldReturnAuthenticationResponse_WhenValidRequestIsProvided() throws Exception {
        String email = "user@example.com";
        String password = "password123";
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .email(email)
                .password(password)
                .build();

        String token = "mock-jwt-token";
        Long userId = 1L;
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(token)
                .userId(userId)
                .email(email)
                .build();

        when(authService.register(registrationRequest)).thenReturn(authenticationResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.email").value(email));

        verify(authService, times(1)).register(registrationRequest);
    }

    @Test
    void login_ShouldReturnAuthenticationResponse_WhenValidRequestIsProvided() throws Exception {
        String email = "user@example.com";
        String password = "password123";
        AuthenticationRequest loginRequest = AuthenticationRequest.builder()
                .email(email)
                .password(password)
                .build();

        String token = "mock-jwt-token";
        Long userId = 1L;
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .token(token)
                .userId(userId)
                .email(email)
                .build();

        when(authService.login(loginRequest)).thenReturn(authenticationResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.email").value(email));

        verify(authService, times(1)).login(loginRequest);
    }
}
