package ua.mctv32.kpi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.mctv32.kpi.domain.User;
import ua.mctv32.kpi.security.UserAuthentication;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    public static final String JWT_USER_ID_CLAIM = "userId";
    public static final String JWT_ROLES_CLAIM = "roles";

    private final SecretKey JWT_SECRET_KEY;
    private final long ACCESS_TOKEN_TLL_MS;
    private final long REFRESH_TOKEN_TLL_MS;

    private final ObjectMapper objectMapper;
    private final Clock clock;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-token-ttl-ms}") long accessTokenTtlMs,
                      @Value("${jwt.refresh-token-ttl-ms}") long refreshTokenTtlMs,
                      ObjectMapper objectMapper, Clock clock) {
        this.JWT_SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
        this.ACCESS_TOKEN_TLL_MS = accessTokenTtlMs;
        this.REFRESH_TOKEN_TLL_MS = refreshTokenTtlMs;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, ACCESS_TOKEN_TLL_MS);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, REFRESH_TOKEN_TLL_MS);
    }

    private String generateToken(User user, long ttl) {
        Instant now = clock.instant();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim(JWT_USER_ID_CLAIM, user.getId())
                .claim(JWT_ROLES_CLAIM, user.getRoles())
                .issuedAt(new Date(now.toEpochMilli()))
                .expiration(new Date(now.plusSeconds(ttl).toEpochMilli()))
                .signWith(JWT_SECRET_KEY)
                .compact();
    }

    public Optional<UserAuthentication> toAuthentication(String accessToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(JWT_SECRET_KEY)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();

            return Optional.of(new UserAuthentication(
                    claims.get(JWT_USER_ID_CLAIM, Long.class),
                    claims.getSubject(),
                    objectMapper.convertValue(claims.get(JWT_ROLES_CLAIM), new TypeReference<>() {
                    }))
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
