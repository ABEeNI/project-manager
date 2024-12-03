package home.projectmanager.service;

import home.projectmanager.controller.auth.AuthenticationRequest;
import home.projectmanager.controller.auth.AuthenticationResponse;
import home.projectmanager.controller.auth.RegistrationRequest;
import home.projectmanager.entity.User;
import home.projectmanager.exception.user.UserAlreadyExistsException;
import home.projectmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegistrationRequest registrationRequest) {
        var user = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .role(registrationRequest.getRole())
                .build();
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User by this Email already exists");
        }
        User savedUser = userRepository.save(user);
        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .build();
    }

    public AuthenticationResponse login(AuthenticationRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .build();
    }
}
