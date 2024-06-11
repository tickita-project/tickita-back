package back.tickita.application.schedule.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRequest {

    @Schema(description = "일정명", example = "회의")
    private String title;

    @Schema(description = "시작일시", example = "2024-06-05T09:00:00")
    private LocalDateTime startDateTime;

    @Schema(description = "종료일시", example = "2024-06-05T10:00:00")
    private LocalDateTime endDateTime;

    @Schema(description = "위치", example = "회의실 A")
    private String location;

    @Schema(description = "설명", example = "프로젝트 회의")
    private String description;

    @Schema(description = "그룹ID", example = "1")
    private Long crewId;

    @Schema(description = "참석자", example = "[{\"accountId\": 1, \"nickName\": \"User1\"}, {\"accountId\": 2, \"nickName\": \"User2\"}]")
    private List<ParticipantInfo> participants;

    @Getter
    public static class ParticipantInfo {
        private Long accountId;
        private String nickName;
    }
}