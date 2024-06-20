package back.tickita.application.crews.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CrewMemberInfoResponse {

    @Schema(description = "그룹 권한", example = "OWNER")
    private String role;

    @Schema(description = "회원 id", example = "1")
    private Long accountId;

    @Schema(description = "닉네임", example = "밍밍")
    private String nickName;

    @Schema(description = "이메일", example = "test@test.com")
    private String email;

    @Schema(description = "imgUrl")
    private String image;
}
