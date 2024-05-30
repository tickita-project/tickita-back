package back.tickita.application.account.dto.response;

import back.tickita.security.oauth.AuthTokens;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private String email;
    private AuthTokens token;

    public LoginResponse(String email, AuthTokens token) {
        this.email = email;
        this.token = token;
    }
}
