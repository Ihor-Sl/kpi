package ua.mctv32.kpi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.mctv32.kpi.service.JwtService;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional.ofNullable(request.getHeader(AUTH_HEADER))
                .map(header -> header.substring(BEARER_PREFIX.length()))
                .flatMap(jwtService::toAuthentication)
                .ifPresent(userAuthentication -> {
                    SecurityContextHolder.getContext().setAuthentication(userAuthentication);
                });

        filterChain.doFilter(request, response);
    }
}
