package back.tickita.application.token.service;

import back.tickita.domain.token.entity.RefreshToken;
import back.tickita.application.token.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Value("${jwt.refreshTokenExpirationMs}")
    private Long refreshTokenExpirationMs;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void saveRefreshToken(String token, String email) {
        Instant expiryDate = Instant.now().plusMillis(refreshTokenExpirationMs);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setEmail(email);
        refreshToken.setExpiryDate(expiryDate);
        refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteRefreshTokenByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

//    public boolean isTokenExpired(RefreshToken token) {
//        return token.getExpiryDate().isBefore(LocalDateTime.now());
//    }
}
