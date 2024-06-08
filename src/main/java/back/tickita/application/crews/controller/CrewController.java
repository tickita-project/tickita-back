package back.tickita.application.crews.controller;

import back.tickita.application.account.dto.request.LoginUserInfo;
import back.tickita.application.crews.dto.request.CrewRequest;
import back.tickita.application.crews.dto.response.CrewAllResponse;
import back.tickita.application.crews.dto.response.CrewCreateResponse;
import back.tickita.application.crews.dto.response.CrewInfoResponse;
import back.tickita.application.crews.service.CrewsReadService;
import back.tickita.application.crews.service.CrewsWriteService;
import back.tickita.interceptor.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{crewId}")
    @Operation(summary = "특정 그룹 상세 정보 조회", description = "특정 그룹의 상세 정보를 조회합니다.")
    public CrewInfoResponse getCrewInfo(@LoginUser LoginUserInfo loginUserInfo, @PathVariable(value = "crewId") Long crewId){
        return crewsReadService.getCrewInfo(loginUserInfo.accountId(), crewId);
    }

    @GetMapping("/all-info")
    @Operation(summary = "사용자가 속한 그룹 전체 조회", description = "사용자가 속한 그룹을 전체 조회합니다.")
    public CrewAllResponse getCrewAllInfo(@LoginUser LoginUserInfo loginUserInfo){
        return crewsReadService.getCrewAll(loginUserInfo.accountId());
    }
}
