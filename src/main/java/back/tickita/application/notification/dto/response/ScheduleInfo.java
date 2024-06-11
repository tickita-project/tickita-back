package back.tickita.application.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleInfo {

    @Schema(description = "일정 조율 확정 시간", example = "2024-06-11T00:24:39.637184")
    private LocalDateTime scheduleTime;

    @Schema(description = "일정 조율 확정 장소", example = "회의실2")
    private String place;
}
