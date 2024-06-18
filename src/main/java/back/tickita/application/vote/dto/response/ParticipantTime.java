package back.tickita.application.vote.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ParticipantTime {

    @Schema(description = "회원 id", example = "1")
    private Long accountId;

    @Schema(description = "회원의 등록된 일정 시작 날짜 및 시간", example = "2024-06-20T14:00:00")
    private LocalDateTime haveStartDateTime;

    @Schema(description = "회원의 등록된 일정 마감 날짜 및 시간", example = "2024-06-20T17:00:00")
    private LocalDateTime haveEndDateTime;

    public void setParticipant(Long accountId, LocalDateTime haveStartDateTime, LocalDateTime haveEndDateTime){
        this.accountId = accountId;
        this.haveStartDateTime = haveStartDateTime;
        this.haveEndDateTime = haveEndDateTime;
    }
}
