package back.tickita.application.vote.dto.response;


import back.tickita.domain.vote.entity.VoteList;
import back.tickita.domain.vote.enums.VoteEndType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class VoteSubjectResponse {

    @Schema(description = "회원id", example = "1")
    private Long accountId;

    @Schema(description = "그룹 id", example = "1")
    private Long crewId;

    @Schema(description = "투표 주제 id", example = "1")
    private Long voteSubjectId;

    @Schema(description = "일정 조율 제목", example = "코드잇 회의")
    private String title;

    @Schema(description = "일정 조율 내용", example = "ㅇㅇ기획 회의")
    private String content;

    @Schema(description = "장소", example = "회의실2")
    private String place;

    @Schema(description = "일정 조율 마감 날짜", example = "2024-06-19")
    private LocalDate endDate;

    @Schema(description = "일정 조율 마감 시간", example = "23:59:00")
    private LocalTime endTime;

    @Schema(description = "투표 마감 여부", example = "PROGRESS / FINISH")
    private VoteEndType voteEndType;

    private List<VoteList> voteLists;
}
