package home.projectmanager.service.auth;

import home.projectmanager.entity.Role;
import home.projectmanager.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Claims;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(JwtServiceTest.JwtServiceTestConfig.class)
@TestPropertySource(properties = {
        "jwt.secret=rg26e1357eefb8bd11542850d66d8007d620e4050b5715dc83f4a921d36ce9ce4730d13c5d85fbb0ff8318d2877eec2f73b931bd47417a81a538327a8927da3e"
})
class JwtServiceTest {

    @TestConfiguration
    static class JwtServiceTestConfig {
        @Bean
        public JwtService jwtService() {
            return new JwtService();
        }
    }

    @Autowired
    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.USER)
                .build();

    }

    @Test
    void shouldGenerateTokenForUser() {
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length, "Token should have three parts (header, payload, signature)");

        String username = jwtService.extractUsername(token);
        assertEquals(user.getEmail(), username);

        assertTrue( jwtService.isTokenValid(token, user));
    }

    @Test
    void shouldExtractClaimsFromToken() {
        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);
        assertEquals(user.getEmail(), username);

        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        assertNotNull(expiration);
    }

    @Disabled("This test is disabled because it is not consistent")
    @Test
    void shouldValidateTokenForUser() {
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void shouldBuildTokenWithExtraClaims() {
        Map<String, Object> extraClaims = Map.of("role", user.getRole().name());

        String token = jwtService.generateToken(extraClaims, user);

        assertNotNull(token);

        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals(Role.USER.name(), role);
    }
}
