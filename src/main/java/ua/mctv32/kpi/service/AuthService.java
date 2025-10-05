package ua.mctv32.kpi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.mctv32.kpi.domain.User;
import ua.mctv32.kpi.dto.AuthResponseDto;
import ua.mctv32.kpi.exception.AuthenticationException;
import ua.mctv32.kpi.repository.UserRepository;
import ua.mctv32.kpi.security.UserAuthentication;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .filter(it -> passwordEncoder.matches(password, it.getPassword()))
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        String authToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(authToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponseDto refreshTokens(String refreshToken) {
        User user = jwtService.toAuthentication(refreshToken)
                .map(UserAuthentication::getId)
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new AuthenticationException("Invalid refresh token"));

        String authToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthResponseDto.builder()
                .accessToken(authToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
