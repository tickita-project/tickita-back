package back.tickita.application.notification.dto.response.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {
    CREW("그룹 초대"), SCHEDULE("일정");
    private final String content;
}
