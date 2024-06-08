package back.tickita.security.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LoginInfo {
    private final Long accountId;
}
