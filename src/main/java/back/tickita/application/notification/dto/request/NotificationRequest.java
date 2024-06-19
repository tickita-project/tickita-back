package back.tickita.application.notification.dto.request;

import back.tickita.domain.crews.enums.CrewAccept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {

    @Schema(description = "알림 id", example = "1")
    private Long notificationId;

    @Schema(description = "그룹 id", example = "1")
    private Long crewId;

    @Schema(description = "그룹 초대 상태값", example = "ACCEPT,DECLINE,WAIT")
    private CrewAccept inviteType;
}
