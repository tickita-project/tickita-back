package back.tickita.application.account.dto.request;

import io.swagger.v3.oas.annotations.Parameter;

public record LoginUserInfo(
        Long accountId
) {
}
