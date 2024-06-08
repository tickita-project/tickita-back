package back.tickita.application.account;

import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.token.entity.Token;
import back.tickita.domain.token.repository.TokenRepository;
import back.tickita.filter.handler.LoginUserDetail;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;
    private Key key;

    public JwtTokenProvider(AccountRepository accountRepository, TokenRepository tokenRepository) {
        this.accountRepository = accountRepository;
        this.tokenRepository = tokenRepository;
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String accessTokenGenerate(String subject, Long expiredAt, String role) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("auth",role)
                .setExpiration(new Date(expiredAt))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    public String refreshTokenGenerate(Long expiredAt, String role) {
        return Jwts.builder()
                .claim("auth",role)
                .setExpiration(new Date(expiredAt))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return true;
        } catch (SecurityException | UnsupportedJwtException | IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ExpiredJwtException e) {
            return false;
        }
        return false;
    }

    public Authentication getAuthentication(String token, LocalDateTime now) {
        Token findToken = tokenRepository.findTokenLoginInfo(token).orElse(null);

        if (findToken == null) {
            return null;
        } else if (now.isAfter(findToken.getAccessExpiredAt())) {
            throw new JwtException("토큰이 만료되었습니다.");
        }
        Account account = accountRepository.findById(getAccount(token))
                .orElse(null);
        if (account == null) {
            return null;
        }
        LoginUserDetail loginUserDetail = new LoginUserDetail(account.getId(), account.getEmail(), account.getRole().name());
        return new UsernamePasswordAuthenticationToken(loginUserDetail, "",
                loginUserDetail.getAuthorities());
    }

    public Long getAccount(String token) {
        return Long.parseLong(
                Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody()
                        .getSubject());
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
