package back.tickita.application.vote.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@NoArgsConstructor
@Setter
public class VoteDateListResponse {

    @Schema(description = "일정 조율 날짜", example = "2024-06-20")
    private LocalDate voteDate;

    @Schema(description = "일정 조율 시작 시간", example = "14:00:00")
    private LocalTime voteStartTime;

    @Schema(description = "일정 조율 마감 시간", example = "17:00:00")
    private LocalTime voteEndTime;

    @Schema(description = "투표 개수", example = "1")
    private int voteCount;

    public VoteDateListResponse(LocalDate voteDate, LocalTime voteStartTime, LocalTime voteEndTime, int voteCount) {
        this.voteDate = voteDate;
        this.voteStartTime = voteStartTime;
        this.voteEndTime = voteEndTime;
        this.voteCount = voteCount;
    }
}
