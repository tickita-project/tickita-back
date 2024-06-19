package back.tickita.application.vote.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CoordinationNotificationResponse {
    @Schema(description = "isChecked가 false인 개수", example = "1")
    private Long count;

    private List<VoteNotificationResponse> voteNotificationResponseList;
}
