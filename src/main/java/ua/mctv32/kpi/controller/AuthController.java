package ua.mctv32.kpi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mctv32.kpi.dto.AuthRequestDto;
import ua.mctv32.kpi.dto.AuthResponseDto;
import ua.mctv32.kpi.dto.RefreshTokensRequestDto;
import ua.mctv32.kpi.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody AuthRequestDto authRequestDto) {
        return authService.login(authRequestDto.getEmail(), authRequestDto.getPassword());
    }

    @PostMapping("/refresh")
    public AuthResponseDto refresh(@RequestBody RefreshTokensRequestDto refreshTokensRequestDto) {
        return authService.refreshTokens(refreshTokensRequestDto.getRefreshToken());
    }
}
