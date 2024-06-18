package back.tickita.application.vote.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteParticipantRequest {

    @Schema(description = "회원id", example = "1")
    private Long accountId;
}
