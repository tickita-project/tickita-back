package back.tickita.application.schedule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ScheduleResponse {

    @Schema(description = "일정ID", example = "1")
    private Long scheduleId;

    @Schema(description = "일정명", example = "회의")
    private String title;

    @Schema(description = "시작일시", example = "2024-06-05T09:00:00")
    private LocalDateTime startTime;

    @Schema(description = "종료일시", example = "2024-06-05T10:00:00")
    private LocalDateTime endTime;

    @Schema(description = "위치", example = "회의실 A")
    private String location;

    @Schema(description = "설명", example = "프로젝트 회의")
    private String description;

    @Schema(description = "그룹ID", example = "1")
    private Long crewId;

    @Schema(description = "참석자", example = "[1, 2]")
    private List<Long> participantIds;
}
