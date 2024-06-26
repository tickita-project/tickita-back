package back.tickita.domain.notification.eums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {
    INVITE("그룹에 초대받았어요. 초대장을 확인해보세요."), SCHEDULE_INFO("일정이 확정되었어요. 일정을 확인해보세요."), REQUEST("일정 조율 요청이 왔어요.일정을 조율해주세요"),
    UPDATE("그룹 일정이 변경되었어요. 변경된 일정을 확인해보세요."), EXCLUDE("조율 요청이 온 일정에 투표하지 않아 일정 참석자에서 제외되었어요");

    private final String content;
}
