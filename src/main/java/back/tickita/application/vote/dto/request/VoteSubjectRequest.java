package back.tickita.application.vote.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public record VoteSubjectRequest (

        @Schema(description = "그룹 id", example = "1")
         Long crewId,
        @Schema(description = "일정 조율 제목", example = "코드잇 회의")
         String title,

        @Schema(description = "일정 조율 내용", example = "ㅇㅇ기획 회의")
         String content,

        @Schema(description = "장소", example = "회의실2")
         String place,

         List<VoteDateList> voteDateLists,

        @Schema(description = "일정 조율 마감 날짜", example = "2024-06-20")
         LocalDate endDate,

        @Schema(description = "일정 조율 마감 시간", example = "17:00:00")
         LocalTime endTime,

         List<Long> accountIds
){

}
