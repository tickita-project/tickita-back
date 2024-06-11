package back.tickita.application.notification.dto.request;

import back.tickita.domain.crews.enums.CrewAccept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {

    private Long notificationId;
    @Schema(description = "그룹 초대 상태값", example = "ACCEPT,DECLINE,WAIT")
    private CrewAccept inviteType;
}
