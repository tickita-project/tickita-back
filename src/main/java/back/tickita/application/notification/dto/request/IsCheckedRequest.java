package back.tickita.application.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IsCheckedRequest {
    @Schema(description = "알림 여부 확인", example = "false/true")
    private Boolean isChecked;

    @Schema(description = "알림 타입", example = "CREW, SCHEDULE")
    private String alarmType;
}
