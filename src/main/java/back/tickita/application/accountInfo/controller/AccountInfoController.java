package back.tickita.application.accountInfo.controller;


import back.tickita.application.accountInfo.dto.request.AccountInfoRequest;
import back.tickita.application.accountInfo.dto.response.AccountInfoResponse;
import back.tickita.application.accountInfo.service.InfoReadService;
import back.tickita.application.accountInfo.service.InfoWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account-info")
@Tag(name = "회원 추가 정보 API", description = "AccountInfoController")
public class AccountInfoController {
    private final InfoWriteService infoWriteService;
    private final InfoReadService infoReadService;

    @PostMapping
    @Operation(summary = "회원 추가 정보 입력", description = "로그인 한 회원의 추가 정보를 등록합니다.")
    public String updateAccountInfo(@RequestParam Long accountId, AccountInfoRequest accountRequest) throws IOException {
        return infoWriteService.updateAccountInfo(accountId, accountRequest);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "회원 추가 정보 조회", description = "로그인 한 회원의 정보를 조회합니다.")
    public AccountInfoResponse findAccountInfo(@PathVariable(value = "accountId") Long accountId){
        return infoReadService.findAccountInfo(accountId);
    }
}
