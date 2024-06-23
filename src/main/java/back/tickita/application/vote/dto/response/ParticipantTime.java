package back.tickita.application.vote.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
// equals, hashcode
// equals :
public class ParticipantTime {


    @Schema(description = "회원의 등록된 일정 시작 날짜 및 시간", example = "2024-06-20 14:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime haveStartDateTime;

    @Schema(description = "회원의 등록된 일정 마감 날짜 및 시간", example = "2024-06-20 17:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime haveEndDateTime;

    public ParticipantTime(LocalDateTime haveStartDateTime, LocalDateTime haveEndDateTime){
        this.haveStartDateTime = haveStartDateTime;
        this.haveEndDateTime = haveEndDateTime;
    }
}
