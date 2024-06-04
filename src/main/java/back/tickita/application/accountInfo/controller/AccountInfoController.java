package back.tickita.application.accountInfo.controller;


import back.tickita.application.accountInfo.dto.ImageRequest;
import back.tickita.application.accountInfo.dto.request.AccountInfoRequest;
import back.tickita.application.accountInfo.dto.response.AccountInfoResponse;
import back.tickita.application.accountInfo.dto.response.AccountResponse;
import back.tickita.application.accountInfo.service.InfoReadService;
import back.tickita.application.accountInfo.service.InfoWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public String updateAccountInfo(@RequestPart(value = "file", required = false) MultipartFile multipartFile , @RequestPart(name = "request") AccountInfoRequest accountRequest) throws IOException {
        return infoWriteService.updateAccountInfo(multipartFile,accountRequest);
    }

    @GetMapping("/info-all/{accountId}")
    @Operation(summary = "회원 추가 정보 조회", description = "로그인 한 회원의 정보를 조회합니다.")
    public AccountInfoResponse findAccountInfo(@PathVariable(value = "accountId") Long accountId){
        return infoReadService.findAccountInfo(accountId);
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "회원 이메일 조회", description = "로그인 한 회원의 이메일을 조회합니다.")
    public AccountResponse findAccountEmail(@PathVariable(value = "accountId") Long accountId){
        return infoReadService.findAccountEmail(accountId);
    }
}
