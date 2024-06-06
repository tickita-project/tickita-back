package back.tickita.application.accountInfo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountResponse {
    @Schema(description = "회원 이메일", example = "test@test.com")
    private String email;
}
