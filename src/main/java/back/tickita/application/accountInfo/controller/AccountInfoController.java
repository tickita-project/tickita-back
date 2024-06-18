package back.tickita.application.accountInfo.controller;


import back.tickita.application.account.dto.request.LoginUserInfo;
import back.tickita.application.accountInfo.dto.request.AccountInfoRequest;
import back.tickita.application.accountInfo.dto.response.AccountImgUrlResponse;
import back.tickita.application.accountInfo.dto.response.AccountInfoResponse;
import back.tickita.application.accountInfo.dto.response.AccountResponse;
import back.tickita.application.accountInfo.service.InfoReadService;
import back.tickita.application.accountInfo.service.InfoWriteService;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import back.tickita.interceptor.annotation.LoginUser;
import back.tickita.security.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    public TokenResponse updateAccountInfo(@RequestBody AccountInfoRequest accountRequest, @Parameter(hidden = true) String role){
        return infoWriteService.updateAccountInfo(accountRequest, role);
    }

    @PostMapping(value = "/img" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원 추가 정보 이미지 등록", description = "로그인 한 회원의 추가 정보 이미지를 등록합니다.")
    public AccountImgUrlResponse updateAccountInfo(@RequestPart(required = false) MultipartFile multipartFile) throws IOException {
        return infoWriteService.updateAccountImg(multipartFile);
    }

    @GetMapping("/all")
    @Operation(summary = "회원 추가 정보 조회", description = "로그인 한 회원의 정보를 조회합니다.")
    public AccountInfoResponse findAccountInfo(@LoginUser LoginUserInfo loginUserInfo){
        return infoReadService.findAccountInfo(loginUserInfo.accountId());
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "회원 이메일 조회", description = "로그인 한 회원의 이메일을 조회합니다.")
    public AccountResponse findAccountEmail(@PathVariable(value = "accountId") Long accountId){
        return infoReadService.findAccountEmail(accountId);
    }

    @PutMapping("/{accountId}")
    @Operation(summary = "회원 정보 수정", description = "로그인 한 회원의 정보를 수정합니다.")
    public AccountInfoResponse updateAccountInfo(@LoginUser LoginUserInfo loginUserInfo, @PathVariable(value = "accountId") Long accountId,
                                                 @RequestBody AccountInfoRequest accountInfoRequest) {
        if (!accountId.equals(loginUserInfo.accountId())) {
            throw new TickitaException(ErrorCode.FORBIDDEN_ACCESS);
        }
        return infoWriteService.updateAccountInfo(accountInfoRequest);
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "해당 토큰을 가진 회원을 탈퇴합니다.")
    public String accountWithdrawal(@LoginUser LoginUserInfo loginUserInfo) {
        return infoWriteService.accountWithdrawal(loginUserInfo.accountId());
    }
}
