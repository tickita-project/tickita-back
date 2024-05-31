package back.tickita.security.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TokenResponse(
        Long id,
        String grantType,
        String accessToken,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime accessTokenExpireAt,
        String refreshToken,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime refreshTokenExpireAt
) {
}
