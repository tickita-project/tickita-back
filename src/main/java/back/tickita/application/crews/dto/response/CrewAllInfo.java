package back.tickita.application.crews.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CrewAllInfo {

    @Schema(description = "그룹 id", example = "1")
    private Long crewId;

    @Schema(description = "그룹 이름", example = "코드잇")
    private String crewName;

    @Schema(description = "그룹 색상", example = "F5C92E")
    private String labelColor;
}
