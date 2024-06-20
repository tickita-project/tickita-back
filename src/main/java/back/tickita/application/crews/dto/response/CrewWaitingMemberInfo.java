package back.tickita.application.crews.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CrewWaitingMemberInfo {

    @Schema(description = "알림 id", example = "1")
    private Long notificationId;

    @Schema(description = "회원 id", example = "1")
    private Long accountId;

    @Schema(description = "닉네임", example = "홍길동")
    private String nickName;

    @Schema(description = "이메일", example = "test@test.com")
    private String email;
}
