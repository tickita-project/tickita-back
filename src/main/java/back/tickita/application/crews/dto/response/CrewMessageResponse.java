package back.tickita.application.crews.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CrewMessageResponse {

    @Schema(description = "그룹ID", example = "1")
    private Long crewId;

    @Schema(description = "메시지", example = "그룹이 성공적으로 삭제되었습니다.")
    private String message;
}
