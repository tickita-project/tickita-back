package back.tickita.application.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CrewNotificationResponse {

    @Schema(description = "알림 id", example = "1")
    private Long notificationId;

    @Schema(description = "알림 타입", example = "INVITE, SCHEDULE")
    private String notificationType;

    @Schema(description = "그룹 id", example = "1")
    private Long crewId;

    @Schema(description = "회원 id", example = "1")
    private Long accountId;

    @Schema(description = "그룹 이름", example = "코드잇")
    private String crewName;

    private List<ScheduleInfo> scheduleInfo;

    @Schema(description = "알림 온 시간", example = "2024-06-11T00:24:39.637184")
    private LocalDateTime localDateTime;

    @Schema(description = "알림 확인 여부", example = "false, true")
    private Boolean isChecked;

    @Schema(description = "link", example = "link")
    private String link;

    @Schema(description = "content", example = "content")
    private String content;

}
