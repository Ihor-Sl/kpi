package ua.mctv32.kpi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.mctv32.kpi.domain.Role;
import ua.mctv32.kpi.domain.User;
import ua.mctv32.kpi.dto.AuthResponseDto;
import ua.mctv32.kpi.exception.AuthenticationException;
import ua.mctv32.kpi.repository.UserRepository;
import ua.mctv32.kpi.security.UserAuthentication;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void givenValidCredentials_whenLogin_thenReturnsAuthResponseDto() {
        // GIVEN
        User user = buildTestUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw-password", "encoded-password")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        // WHEN
        AuthResponseDto result = authService.login("test@example.com", "raw-password");

        // THEN
        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
    }

    @Test
    void givenInvalidEmail_whenLogin_thenThrowsAuthenticationException() {
        // GIVEN
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // WHEN / THEN
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authService.login("notfound@example.com", "password")
        );
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void givenInvalidPassword_whenLogin_thenThrowsAuthenticationException() {
        // GIVEN
        User user = buildTestUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        // WHEN / THEN
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authService.login("test@example.com", "wrong-password")
        );
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void givenValidRefreshToken_whenRefreshTokens_thenReturnsNewTokens() {
        // GIVEN
        User user = buildTestUser();
        String refreshToken = "valid-refresh-token";
        UserAuthentication userAuth = new UserAuthentication(1L, "test@example.com", Set.of(Role.ROLE_USER));

        when(jwtService.toAuthentication(refreshToken)).thenReturn(Optional.of(userAuth));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");

        // WHEN
        AuthResponseDto result = authService.refreshTokens(refreshToken);

        // THEN
        assertNotNull(result);
        assertEquals("new-access-token", result.getAccessToken());
        assertEquals("new-refresh-token", result.getRefreshToken());
    }

    @Test
    void givenInvalidRefreshToken_whenRefreshTokens_thenThrowsAuthenticationException() {
        // GIVEN
        when(jwtService.toAuthentication("invalid-token")).thenReturn(Optional.empty());

        // WHEN / THEN
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authService.refreshTokens("invalid-token")
        );
        assertEquals("Invalid refresh token", exception.getMessage());
    }

    @Test
    void givenValidAuthButUserNotFound_whenRefreshTokens_thenThrowsAuthenticationException() {
        // GIVEN
        String refreshToken = "valid-refresh-token";
        UserAuthentication userAuth = new UserAuthentication(1L, "test@example.com", Set.of(Role.ROLE_USER));

        when(jwtService.toAuthentication(refreshToken)).thenReturn(Optional.of(userAuth));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN / THEN
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authService.refreshTokens(refreshToken)
        );
        assertEquals("Invalid refresh token", exception.getMessage());
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
