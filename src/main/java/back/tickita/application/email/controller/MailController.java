package back.tickita.application.email.controller;

import back.tickita.application.account.dto.request.LoginUserInfo;
import back.tickita.application.email.dto.request.SendEmail;
import back.tickita.application.email.service.MailService;

import back.tickita.interceptor.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/mail")
@Tag(name = "이메일 발송 API", description = "MailController")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/send/{crewId}")
    @Operation(summary = "이메일 발송", description = "회원 가입 되어 있는 회원을 그룹에 초대하면 이메일이 발송됩니다.")
    public ResponseEntity<String> setInviteCrew(@LoginUser LoginUserInfo loginUserInfo, @RequestBody SendEmail email, @PathVariable(value = "crewId") Long crewId){
        mailService.setInviteCrew(loginUserInfo.accountId(), email.email(), crewId);
        return new ResponseEntity<>("초대 메일 발송 성공", HttpStatus.OK);
    }
}
