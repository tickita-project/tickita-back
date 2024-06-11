package back.tickita.application.schedule.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private LocalDateTime startDateTime;

    @Schema(description = "종료일시", example = "2024-06-05T10:00:00")
    private LocalDateTime endDateTime;

    @Schema(description = "위치", example = "회의실 A")
    private String location;

    @Schema(description = "설명", example = "프로젝트 회의")
    private String description;

    @Schema(description = "그룹정보", example = "{\"crewId\" : 1, \"crewName\" : \"코드잇\", \"labelColor\":\"F5C92E\"}")
    private CrewInfo crewInfo;

    @Schema(description = "참석자", example = "[{\"accountId\": 1, \"nickName\": \"User1\"}, {\"accountId\": 2, \"nickName\": \"User2\"}]")
    private List<ParticipantInfo> participants;

    public ScheduleResponse(Long scheduleId, String title, LocalDateTime startDateTime, LocalDateTime endDateTime,
                            String location, String description, Long crewId, String crewName, String labelColor,
                            List<ParticipantInfo> participants) {
        this.scheduleId = scheduleId;
        this.title = title;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.description = description;
        this.crewInfo = new CrewInfo(crewId, crewName, labelColor);
        this.participants = participants;
    }

    public static class CrewInfo {

        @JsonProperty("crewId")
        private Long crewId;

        @JsonProperty("crewName")
        private String crewName;

        @JsonProperty("labelColor")
        private String labelColor;

        public CrewInfo(Long crewId, String crewName, String labelColor) {
            this.crewId = crewId;
            this.crewName = crewName;
            this.labelColor = labelColor;
        }
    }

    public static class ParticipantInfo {
        @JsonProperty("accountId")
        private Long accountId;

        @JsonProperty("nickName")
        private String nickName;

        public ParticipantInfo(Long accountId, String nickName) {
            this.accountId = accountId;
            this.nickName = nickName;
        }
    }
}
