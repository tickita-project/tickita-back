package back.tickita.dto.token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshRequest {

    private String refreshToken;

    private String expiredAccessToken;
}
