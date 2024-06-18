package back.tickita.application.vote.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class VoteStateRequest {

    @Schema(description = "그룹 id", example = "1")
    private Long crewId;

    private List<Long> voteStateIds; // 사용자가 가능한 회의 날
}

