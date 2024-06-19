package back.tickita.application.vote.dto.response;

import back.tickita.domain.vote.enums.VoteEndType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class VoteMypageResponse {

    @Schema(description = "그룹id", example = "1")
    private Long crewId;

    @Schema(description = "그룹 이름", example = "코드잇")
    private String crewName;

    @Schema(description = "일정 조율 제목", example = "코드잇 회의")
    private String title;

    @Schema(description = "일정 조율 생성자 id", example = "1")
    private Long voteCreatorId;

    @Schema(description = "일정 조율 생성자 닉네임", example = "밍밍")
    private String voteCreatorName;

    @Schema(description = "일정 조율 마감시간", example = "23:59:00")
    private LocalTime endTime;

    @Schema(description = "일정 조율 마감 날짜", example = "1")
    private LocalDate endDate;

    @Schema(description = "투표 참여 여부", example = "1")
    Boolean isVote;
}
