package back.tickita.security.oauth;

import back.tickita.application.account.JwtTokenProvider;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.token.entity.Token;
import back.tickita.domain.token.repository.TokenRepository;
import back.tickita.exception.TickitaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {

    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private String BEARER_TYPE = "Bearer";
    private long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60;    //1시간
    private long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14;  // 14일

    //id 받아 Access Token 생성
    public AuthTokens generate(Long accountId) {
        long now = System.currentTimeMillis();
        Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        Token token = tokenRepository.findByAccountId(accountId).orElse(null);

        if (token != null) {
            if (isAccessTokenExpired(token)) {
                throw new TickitaException("토큰 만료됨");
            }
            String newAccessToken = jwtTokenProvider.accessTokenGenerate(accountId.toString(), accessTokenExpiredAt);
            token.setAccess(newAccessToken);
            token.setAccessExpiredAt(accessTokenExpiredAt);
            tokenRepository.save(token);
            return AuthTokens.of(newAccessToken, token.getRefresh(), BEARER_TYPE, ACCESS_TOKEN_EXPIRE_TIME / 1000L);
        } else {

            String accessToken = jwtTokenProvider.accessTokenGenerate(accountId.toString(), accessTokenExpiredAt);
            String refreshToken = jwtTokenProvider.refreshTokenGenerate(refreshTokenExpiredAt);

            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new TickitaException("회원이 존재하지 않습니다."));
            System.out.println("Account found with ID: " + account.getId());
            tokenRepository.save(new Token(accessToken, refreshToken, accessTokenExpiredAt, refreshTokenExpiredAt, account));

            return AuthTokens.of(accessToken, refreshToken, BEARER_TYPE, ACCESS_TOKEN_EXPIRE_TIME / 1000L);
        }
    }
    private boolean isAccessTokenExpired(Token token) {
        return token.getAccessExpiredAt().before(new Date());
    }
}
