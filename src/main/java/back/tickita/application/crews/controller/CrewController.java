package back.tickita.application.crews.controller;

import back.tickita.application.account.dto.request.LoginUserInfo;
import back.tickita.application.crews.dto.request.CrewRequest;
import back.tickita.application.crews.dto.response.*;
import back.tickita.application.crews.service.CrewsReadService;
import back.tickita.application.crews.service.CrewsWriteService;
import back.tickita.interceptor.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@Secured("ROLE_USER")
@RequiredArgsConstructor
@RequestMapping("/crew")
@Tag(name = "그룹 생성 API", description = "CrewController")
public class CrewController {
    private final CrewsWriteService crewsWriteService;
    private final CrewsReadService crewsReadService;
    @PostMapping
    @Operation(summary = "그룹 생성", description = "로그인 한 회원이 원하는 그룹을 생성합니다.")
    public CrewCreateResponse create(@LoginUser LoginUserInfo loginUserInfo, @Valid @RequestBody CrewRequest crewRequest){
        return crewsWriteService.create(loginUserInfo.accountId(), crewRequest);
    }

//    @GetMapping("/{crewId}")
//    @Operation(summary = "특정 그룹 상세 정보 조회", description = "특정 그룹의 상세 정보를 조회합니다.")
//    public CrewInfoResponse getCrewInfo(@LoginUser LoginUserInfo loginUserInfo, @PathVariable(value = "crewId") Long crewId){
//        return crewsReadService.getCrewInfo(loginUserInfo.accountId(), crewId);
//    }

    @GetMapping("/all-info")
    @Operation(summary = "사용자가 속한 그룹 전체 조회", description = "사용자가 속한 그룹을 전체 조회합니다.")
    public CrewAllResponse getCrewAllInfo(@LoginUser LoginUserInfo loginUserInfo){
        return crewsReadService.getCrewAll(loginUserInfo.accountId());
    }

    @GetMapping("/{crewId}")
    @Operation(summary = "특정 그룹 상세 정보 조회", description = "특정 그룹의 상세 정보를 조회합니다.")
    public CrewDetailResponse getCrewDetails(@LoginUser LoginUserInfo loginUserInfo, @PathVariable(value = "crewId") Long crewId) {
        return crewsReadService.getCrewDetails(loginUserInfo.accountId(), crewId);
    }

    @PutMapping("/info/{crewId}")
    @Operation(summary = "그룹 정보 수정", description = "그룹의 정보를 수정합니다.")
    public CrewAllInfo updateCrewBasicInfo(@LoginUser LoginUserInfo loginUserInfo, @PathVariable(value = "crewId") Long crewId, @RequestBody CrewRequest crewInfoRequest) {
        return crewsWriteService.updateCrewInfo(crewId, loginUserInfo.accountId(), crewInfoRequest);
    }

    @PutMapping("/delegate-owner/{crewId}")
    @Operation(summary = "그룹장 권한 위임", description = "그룹장 권한을 멤버에게 위임합니다.")
    public CrewMemberInfoResponse delegateOwner(@LoginUser LoginUserInfo loginUserInfo, @PathVariable(value = "crewId") Long crewId, @RequestParam(value = "memberId") Long memberId) {
        return crewsWriteService.delegateOwner(crewId, loginUserInfo.accountId(), memberId);
    }

    @DeleteMapping("/leave/{crewId}")
    @Operation(summary = "그룹 나가기", description = "사용자가 그룹을 탈퇴합니다.")
    public ResponseEntity<CrewMessageResponse> leaveCrew(@LoginUser LoginUserInfo loginUserInfo, @PathVariable(value = "crewId") Long crewId) {
        return ResponseEntity.ok(crewsWriteService.leaveCrew(crewId, loginUserInfo.accountId()));
    }

    @DeleteMapping("remove-member/{crewId}/{memberId}")
    @Operation(summary = "그룹 내보내기", description = "그룹장이 멤버를 탈퇴시킵니다.")
    public ResponseEntity<CrewMessageResponse> removeMember(@LoginUser LoginUserInfo loginUserInfo, @PathVariable(value = "crewId") Long crewId,
                                                            @PathVariable(value = "memberId") Long memberId) {
        return ResponseEntity.ok(crewsWriteService.removeMember(crewId, loginUserInfo.accountId(), memberId));
    }

    @DeleteMapping("/{crewId}")
    @Operation(summary = "그룹 삭제", description = "그룹장이 그룹을 삭제합니다.")
    public ResponseEntity<CrewMessageResponse> deleteCrew(@LoginUser LoginUserInfo loginUserInfo, @PathVariable(value = "crewId") Long crewId) {
        return ResponseEntity.ok(crewsWriteService.deleteCrew(crewId, loginUserInfo.accountId()));
    }
}
