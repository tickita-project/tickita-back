package back.tickita.application.notification.dto.response;


import back.tickita.domain.notification.eums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CoordinationResponse {
    private Long notificationId;
    private Long voteId;
    private Long crewId;
    private Long crewName;
    private NotificationType notificationType;
    private String voteTitle;
}
