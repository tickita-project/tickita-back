package back.tickita.application.vote.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class VoteStateResponse {

    @Schema(description = "일정 조율 제목", example = "코드잇 회의")
    private String title;

    @Schema(description = "일정 조율 내용", example = "ㅇㅇ기획 회의")
    private String content;

    @Schema(description = "장소", example = "회의실2")
    private String place;

    @Schema(description = "그룹id", example = "1")
    private Long crewId;

    @Schema(description = "그룹 이름", example = "코드잇")
    private String crewName;

    @Schema(description = "일정 조율 생성자 id", example = "1")
    private Long creatorId;

    private List<VoteListResponse> voteListResponses;

    @Schema(description = "일정 조율 마감 시간", example = "23:59:00")
    private LocalTime endTime;

    @Schema(description = "일정 조율 마감 날짜", example = "2024-06-19")
    private LocalDate endDate;

    private List<VoteDateListResponse> voteDateListResponses;

    @Schema(description = "투표 개수", example = "1")
    private int voteCount;

    @Schema(description = "마감까지 남은 시간", example = "1일 남음")
    private String remainTime;

    public void setRemainTime(String remainTime) {
        this.remainTime = remainTime;
    }
}
