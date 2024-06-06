package back.tickita.application.accountInfo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class AccountInfoRequest {
    @Schema(description = "회원 id", example = "1")
    private Long accountId;

    @Schema(description = "회원 닉네임", example = "밍밍")
    private String nickName;

    @Schema(description = "회원 전화번호", example = "01012345678")
    private String phoneNumber;
}
