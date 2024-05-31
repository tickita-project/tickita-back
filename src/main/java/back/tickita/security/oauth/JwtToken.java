package back.tickita.security.oauth;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class JwtToken {

    private String grantType;

    private String accessToken;

    private String refreshToken;

}
