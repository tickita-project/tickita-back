package back.tickita.application.vote.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoteListResponse {

    @Schema(description = "회원id", example = "1")
    private Long accountId;

    @Schema(description = "닉네임", example = "밍밍")
    private String nickName;

    @Schema(description = "참석자 투표 완료 여부", example = "false / true")
    private Boolean voteParticipateType;
}
