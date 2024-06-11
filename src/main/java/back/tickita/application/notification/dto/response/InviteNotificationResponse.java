package back.tickita.application.notification.dto.response;

import back.tickita.domain.crews.enums.CrewAccept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InviteNotificationResponse {

    @Schema(description = "회원 Id", example = "1")
    private Long accountId;

    @Schema(description = "그룹 id", example = "1")
    private Long crewId;

    @Schema(description = "그룹 초대 상태값", example = "ACCEPT,DECLINE,WAIT")
    private CrewAccept inviteType;
}
