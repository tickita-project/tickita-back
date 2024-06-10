package back.tickita.application.schedule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FilteredScheduleResponse {

    @Schema(description = "그룹ID", example = "1")
    private Long crewId;

    @Schema(description = "그룹 내 일정 목록")
    private List<ScheduleResponse> schedules;
}
