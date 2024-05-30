package back.tickita.config.jwt;

import back.tickita.dto.token.JwtToken;
import back.tickita.model.account.AccountDetails;
import back.tickita.service.token.RefreshTokenService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String key, RefreshTokenService refreshTokenService,
                            UserDetailsService userDetailsService) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
    }

    public String generateTokens(Authentication authentication) throws JsonProcessingException {

        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(authentication);

        refreshTokenService.saveRefreshToken(refreshToken, authentication.getName());

        JwtToken jwtToken = new JwtToken("Bearer", accessToken, refreshToken);
        return objectMapper.writeValueAsString(jwtToken);
    }

    public String generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + 3600000 * 2); // 2 hours

        Map<String, Object> accessTokenClaims = new HashMap<>();
        accessTokenClaims.put("sub", authentication.getName()); // email
        accessTokenClaims.put("auth", authorities);
        accessTokenClaims.put("exp", accessTokenExpiresIn.getTime() / 1000);

        return createToken(accessTokenClaims, accessTokenExpiresIn);
    }

    private String generateRefreshToken(Authentication authentication) {

        long now = (new Date()).getTime();
        Date refreshTokenExpiresIn = new Date(now + 86400000 * 7); // 7 days

        Map<String, Object> refreshTokenClaims = new HashMap<>();
        refreshTokenClaims.put("sub", authentication.getName()); // email
        refreshTokenClaims.put("exp", refreshTokenExpiresIn.getTime() / 1000);

        return createToken(refreshTokenClaims, refreshTokenExpiresIn);
    }

    private String createToken(Map<String, Object> claims, Date expiryDate) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public JwtToken refreshAccessToken(String expiredAccessToken, String refreshToken) {
        if (!validateToken(refreshToken)) {
            deleteRefreshTokenByToken(refreshToken);
            return null;
        }

        Claims refreshTokenClaims = parseClaims(refreshToken);
        String subject = refreshTokenClaims.getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String newAccessToken = generateAccessToken(authenticationToken);
        String newRefreshToken = updateRefreshToken(userDetails);

        deleteRefreshTokenByToken(refreshToken);
        refreshTokenService.saveRefreshToken(newRefreshToken, subject);

        return new JwtToken("Bearer", newAccessToken, newRefreshToken);
    }

    private String updateRefreshToken(UserDetails userDetails) {
        return UUID.randomUUID().toString();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        UserDetails principal = new AccountDetails(claims.getSubject(), null, authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    @Transactional
    public void deleteRefreshTokenByToken(String token) {
        refreshTokenService.deleteRefreshTokenByToken(token);
    }
}