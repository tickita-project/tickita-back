package back.tickita.application.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleInfo {

    @Schema(description = "일정 id", example = "1")
    private Long scheduleId;

    @Schema(description = "일정 제목", example = "코드잇 회의")
    private String title;
}
