package back.tickita.domain.notification.eums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CoordinationType {
    REQUEST("일정 조율 요청이 왔어요.일정을 조율해주세요"), EXCLUDE("조율 요청이 온 일정에 투표하지 않아 일정 참석자에서 제외되었어요");

    private final String content;
}
