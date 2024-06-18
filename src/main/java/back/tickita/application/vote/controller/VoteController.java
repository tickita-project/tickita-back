package back.tickita.application.vote.controller;


import back.tickita.application.account.dto.request.LoginUserInfo;
import back.tickita.application.vote.dto.request.VoteStateRequest;
import back.tickita.application.vote.dto.request.VoteSubjectRequest;
import back.tickita.application.vote.dto.response.VoteParticipantTimeList;
import back.tickita.application.vote.dto.response.VoteStateResponse;
import back.tickita.application.vote.dto.response.VoteSubjectResponse;
import back.tickita.application.vote.service.VoteReadService;
import back.tickita.application.vote.service.VoteWriteService;
import back.tickita.domain.vote.entity.VoteSubject;
import back.tickita.interceptor.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vote")
@Tag(name = "일정 조율 API", description = "VoteController")
public class VoteController {

    private final VoteWriteService voteWriteService;
    private final VoteReadService voteReadService;

    @PostMapping
    @Operation(summary = "일정 조율 생성 (투표 생성)", description = "그룹의 회원이 일정 조율(투표)을 생성합니다.")
    public void create(@LoginUser LoginUserInfo loginUserInfo, @RequestBody VoteSubjectRequest voteSubjectRequest){
       voteWriteService.create(loginUserInfo.accountId(), voteSubjectRequest);
    }

    @GetMapping("/{crewId}")
    @Operation(summary = "일정 조율 투표 정보 조회", description = "일정 조율 정보(투표 현황) 조회합니다.")
    public VoteStateResponse findVoteState(@LoginUser LoginUserInfo loginUserInfo, @PathVariable("crewId") Long crewId){
        return voteReadService.findVoteState(loginUserInfo.accountId(), crewId);
    }

    @PostMapping("/{voteSubjectId}")
    @Operation(summary = "일정 조율 투표 (참석자)", description = "참석자는 일정 조율 생성된 정보를 투표할 수 있습니다.")
    public void setVote(@LoginUser LoginUserInfo loginUserInfo,@PathVariable(name = "voteSubjectId") Long voteSubjectId, @RequestBody VoteStateRequest voteStateRequest){
        voteWriteService.setVote(loginUserInfo.accountId(), voteStateRequest, voteSubjectId);
    }

    @GetMapping("/{crewId}/{voteSubjectId}")
    @Operation(summary = "일정 조율 참석자 일정 리스트", description = "일정 조율 참석자의 일정 있는 리스트를 조회합니다.")
    public VoteParticipantTimeList findParticipantTime(@PathVariable(value = "crewId") Long crewId, @PathVariable(value = "voteSubjectId") Long voteSubjectId){
        return voteReadService.findParticipantTime(crewId, voteSubjectId);
    }


}
