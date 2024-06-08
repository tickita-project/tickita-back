package back.tickita.application.crews.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CrewInfoResponse {

    @Schema(description = "그룹 이름", example = "코드잇")
    private String crewName;

    @Schema(description = "그룹 색상", example = "F5C92E")
    private String labelColor;

    private List<CrewMemberInfoResponse> crewMemberInfoResponses;
}
