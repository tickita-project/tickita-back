package back.tickita.security.oauth;

import back.tickita.application.account.JwtTokenProvider;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.token.entity.Token;
import back.tickita.domain.token.repository.TokenRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import back.tickita.security.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class AuthTokensGenerator {

    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String GRANT_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1800;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 604800;

    //id 받아 Access Token 생성
    public TokenResponse generate(Long accountId, LocalDateTime now, boolean isComplete, String role) {
        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
        LocalDateTime koreaNow = now.atZone(ZoneId.systemDefault()).withZoneSameInstant(koreaZoneId).toLocalDateTime();
        ZoneOffset koreaZoneOffset = koreaZoneId.getRules().getOffset(koreaNow);

        LocalDateTime accessTokenExpiredAt = koreaNow.plus(ACCESS_TOKEN_EXPIRE_TIME, ChronoUnit.SECONDS);
        LocalDateTime refreshTokenExpiredAt =  koreaNow.plus(REFRESH_TOKEN_EXPIRE_TIME, ChronoUnit.SECONDS);

        String accessToken = jwtTokenProvider.accessTokenGenerate(accountId.toString(), accessTokenExpiredAt.toInstant(koreaZoneOffset).toEpochMilli(),role);
        String refreshToken = jwtTokenProvider.refreshTokenGenerate(refreshTokenExpiredAt.toInstant(koreaZoneOffset).toEpochMilli(),role);

        Token findToken = tokenRepository.findByAccountId(accountId)
                .orElse(null);
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (findToken == null) {
            saveToken(accountId, accessToken, accessTokenExpiredAt, refreshToken, refreshTokenExpiredAt);
            return new TokenResponse(accountId, GRANT_TYPE, accessToken, accessTokenExpiredAt, refreshToken, refreshTokenExpiredAt, isComplete, account.getEmail(), account.getImage(), account.getNickName(), account.getPhoneNumber());
        }
            updateTokenProcess(koreaNow, findToken, accessToken, accessTokenExpiredAt, refreshToken,
                    refreshTokenExpiredAt);

            return new TokenResponse(accountId, GRANT_TYPE, findToken.getAccess(), findToken.getAccessExpiredAt(), findToken.getRefresh(), findToken.getRefreshExpiredAt(), isComplete, account.getEmail(), account.getImage(), account.getNickName(), account.getPhoneNumber());
    }

    private void updateTokenProcess(LocalDateTime now, Token findToken, String accessToken,
                                    LocalDateTime accessTokenExpireAt, String refreshToken,
                                    LocalDateTime refreshTokenExpireAt) {
        if (findToken.getAccessExpiredAt().isBefore(now)) {
            if(findToken.getRefreshExpiredAt().isBefore(now)) {
                updateToken(findToken, accessToken, accessTokenExpireAt, refreshToken,
                        refreshTokenExpireAt);
            }else {
                updateToken(findToken, accessToken, accessTokenExpireAt);
            }
        }
    }

    private void updateToken(Token token, String accessToken, LocalDateTime accessTokenExpireAt, String refreshToken, LocalDateTime refreshTokenExpireAt) {
        token.updateRefreshToken(refreshToken,refreshTokenExpireAt);
        token.updateAccessToken(accessToken, accessTokenExpireAt);
    }
    private void updateToken(Token token, String accessToken, LocalDateTime accessTokenExpireAt) {
        token.updateAccessToken(accessToken, accessTokenExpireAt);
    }

    private void saveToken(Long id, String accessToken, LocalDateTime accessTokenExpireAt,
                           String refreshToken, LocalDateTime refreshTokenExpireAt) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        tokenRepository.save(Token.create(accessToken, accessTokenExpireAt, account, refreshToken, refreshTokenExpireAt));
    }
}
