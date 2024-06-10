package back.tickita.application.schedule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageResponse {

    @Schema(description = "일정ID", example = "1")
    private Long scheduleId;

    @Schema(description = "메시지", example = "일정이 성공적으로 삭제되었습니다.")
    private String message;
}
