package back.tickita.application.notification.dto.response;

import back.tickita.application.notification.dto.response.enums.AlarmType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class NotificationInfo {

    @Schema(description = "알림 id", example = "1")
    private Long notificationId;

    @Schema(description = "알림 메시지 타입", example = "INVITE, SCHEDULE_INFO, REQUEST, UPDATE, EXCLUDE")
    private String notificationType;

    @Schema(description = "그룹 id", example = "1")
    private Long crewId;

    @Schema(description = "그룹 색상", example = "F5C92E")
    private String labelColor;

    @Schema(description = "그룹 이름", example = "코드잇")
    private String crewName;

    private ScheduleInfo scheduleInfo;

    @Schema(description = "알림 온 시간", example = "2024-06-11T00:24:39.637184")
    private LocalDateTime localDateTime;

    @Schema(description = "알림 확인 여부", example = "false, true")
    private Boolean isChecked;

    @Schema(description = "content", example = "content")
    private String content;

    @Schema(description = "알림 타입", example = "CREW, SCHEDULE")
    private AlarmType alarmType;

    public void createScheduleInfo(ScheduleInfo scheduleInfo) {
        this.scheduleInfo = scheduleInfo;
    }
}
