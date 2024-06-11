package back.tickita.domain.crews.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public enum CrewRole {
    OWNER(0), MEMBER(1);
    private final int order;
}
