package back.tickita.application.vote.dto.response;

import back.tickita.domain.vote.enums.VoteEndType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class VoteMypageResponse {
    private Long crewId;
    private String title;
    private Long creatorId;
    private LocalTime endTime;
    private LocalDate endDate;
    Boolean voteParticipateType;
}
