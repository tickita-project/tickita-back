package back.tickita.application.vote.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class VoteNotificationResponse {

    @Schema(description = "알림 id", example = "1")
    private Long notificationId;

    @Schema(description = "알림 타입", example = "INVITE, SCHEDULE")
    private String notificationType;

    @Schema(description = "그룹 id", example = "1")
    private Long crewId;

    @Schema(description = "회원 id", example = "1")
    private Long accountId;

    @Schema(description = "그룹 이름", example = "코드잇")
    private String crewName;

    @Schema(description = "알림 온 시간", example = "2024-06-11T00:24:39.637184")
    private LocalDateTime localDateTime;

    @Schema(description = "알림 확인 여부", example = "false, true")
    private Boolean isChecked;

    @Schema(description = "투표 id", example = "1")
    private Long voteId;

    @Schema(description = "일정 조율(투표) 제목", example = "코드잇 회의")
    private String voteTitle;

    @Schema(description = "회원 투표 여부", example = "false,true")
    private Boolean voteParticipateType;


    public void setVoteNotification(Long notificationId, String notificationType, Long crewId, Long accountId, String crewName, LocalDateTime localDateTime, Boolean isChecked, Long voteId, String voteTitle ,Boolean voteParticipateType) {
        this.notificationId = notificationId;
        this.notificationType = notificationType;
        this.crewId = crewId;
        this.accountId = accountId;
        this.crewName = crewName;
        this.localDateTime = localDateTime;
        this.isChecked = isChecked;
        this.voteId = voteId;
        this.voteTitle = voteTitle;
        this.voteParticipateType = voteParticipateType;
    }
}
