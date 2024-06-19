package back.tickita.application.dashboard.dto;

import back.tickita.application.crews.dto.response.CrewAllInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventInfo {

    @Schema(description = "일정ID", example = "1")
    private Long scheduleId;

    @Schema(description = "일정명", example = "회의")
    private String title;

    @Schema(description = "시작일시", example = "2024-06-05T09:00:00")
    private LocalDateTime startDateTime;

    @Schema(description = "남은시간", example = "D-1, D-DAY")
    private String remainTime;

    @Schema(description = "그룹정보", example = "{\"crewId\" : 1, \"crewName\" : \"코드잇\", \"labelColor\":\"F5C92E\"}")
    private CrewAllInfo crewInfo;
}
