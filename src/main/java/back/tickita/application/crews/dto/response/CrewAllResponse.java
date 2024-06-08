package back.tickita.application.crews.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CrewAllResponse {
    private List<CrewAllInfo> crewAllInfos;
}
