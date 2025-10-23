package ua.mctv32.kpi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.mctv32.kpi.domain.Role;
import ua.mctv32.kpi.domain.User;
import ua.mctv32.kpi.security.UserAuthentication;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String SECRET = "12345678901234567890123456789012";
    private static final long ACCESS_TTL_MS = TimeUnit.MINUTES.toMillis(10);
    private static final long REFRESH_TTL_MS = TimeUnit.DAYS.toMillis(30);
    private static final Instant TIME = Instant.parse("2000-01-01T10:00:00Z");

    @Mock
    private Clock clock;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, ACCESS_TTL_MS, REFRESH_TTL_MS, objectMapper, clock);
    }

    @Test
    void givenUser_whenGenerateAccessToken_thenShouldGenerateAccessToken() {
        // GIVEN
        when(clock.instant()).thenReturn(TIME);
        User user = buildTestUser();

        // WHEN
        String token = jwtService.generateAccessToken(user);

        // THEN
        String expectedAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWF0Ijo5NDY3MjA4MDAsImV4cCI6OTQ2NzIxNDAwfQ.UbqzCvMmyNg-ai76U9e4_sFIA7lJxTw6871aR3B2QRw";
        assertEquals(expectedAccessToken, token);
    }

    @Test
    void givenUser_whenGenerateRefreshToken_thenShouldGenerateRefreshToken() {
        // GIVEN
        when(clock.instant()).thenReturn(TIME);
        User user = buildTestUser();

        // WHEN
        String token = jwtService.generateRefreshToken(user);

        // THEN
        String expectedRefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWF0Ijo5NDY3MjA4MDAsImV4cCI6OTQ5MzEyODAwfQ.MfjSqsPvPcCw_QzP7PS49WNZMiaMApQLOeVj-FRufXM";
        assertEquals(expectedRefreshToken, token);
    }

    @Test
    void givenValidToken_whenToAuthentication_thenShouldReturnOptionalWithUserAuthentication() {
        // GIVEN
        when(clock.instant()).thenReturn(TIME);
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWF0Ijo5NDY3MjA4MDAsImV4cCI6OTQ5MzEyODAwfQ.MfjSqsPvPcCw_QzP7PS49WNZMiaMApQLOeVj-FRufXM";

        // WHEN
        Optional<UserAuthentication> authentication = jwtService.toAuthentication(accessToken);

        // THEN
        assertTrue(authentication.isPresent());

        UserAuthentication expectedUserAuthentication = new UserAuthentication(1L, "test@example.com", Set.of(Role.ROLE_USER));
        assertEquals(expectedUserAuthentication, authentication.get());
    }

    @Test
    void givenInvalidToken_whenToAuthentication_thenShouldReturnEmptyOptional() {
        // GIVEN
        String invalidAccessToken = "invalid";

        // WHEN
        Optional<UserAuthentication> authentication = jwtService.toAuthentication(invalidAccessToken);

        // THEN
        assertFalse(authentication.isPresent());
    }

    @Test
    void givenExpiredToken_whenToAuthentication_thenShouldReturnEmptyOptional() {
        // GIVEN
        when(clock.instant()).thenReturn(TIME.plusMillis(ACCESS_TTL_MS * 2));

        String expiredAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJyb2xlcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImlhdCI6OTQ2NzIwODAwLCJleHAiOjk0NjcyMTQwMH0.487SyjjA_JrBIAw5YcFOt5iwFLmT4tY-_zdXgBvMHPM";

        // WHEN
        Optional<UserAuthentication> authentication = jwtService.toAuthentication(expiredAccessToken);

        // THEN
        assertFalse(authentication.isPresent());
    }

    private User buildTestUser() {
        return User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded-password")
                .roles(Set.of(Role.ROLE_USER))
                .build();
    }
}
