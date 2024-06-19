package back.tickita.application.notification.controller;

import back.tickita.application.account.dto.request.LoginUserInfo;
import back.tickita.application.notification.dto.request.InviteAcceptWitdrawlRequest;
import back.tickita.application.notification.dto.request.NotificationRequest;
import back.tickita.application.notification.dto.response.InviteNotificationResponse;
import back.tickita.application.notification.dto.response.NotificationResponse;
import back.tickita.application.notification.service.NotificationReadService;
import back.tickita.application.notification.service.NotificationWriteService;
import back.tickita.interceptor.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Tag(name = "알림 API", description = "NotificationController")
public class NotificationController {

    private final NotificationReadService NotificationReadService;
    private final NotificationWriteService notificationWriteService;

    @GetMapping
    @Operation(summary = "전체 알림 조회", description = "로그인 한 회원의 전체 알림을 조회합니다.")
    public NotificationResponse getInvite(@LoginUser LoginUserInfo loginUserInfo){
        return NotificationReadService.findAllNotification(loginUserInfo.accountId());
    }

    @PostMapping
    @Operation(summary = "그룹 초대 알림 수락/거절", description = "그룹 초대가 온 회원이 수락/거절을 선택합니다.")
    public InviteNotificationResponse setInviteAccept(@LoginUser LoginUserInfo loginUserInfo, @RequestBody NotificationRequest notificationRequest){
        return notificationWriteService.setInviteAccept(loginUserInfo.accountId(), notificationRequest);
    }

    @DeleteMapping
    @Operation(summary = "그룹 초대 삭제", description = "잘못 초대한 회원()을 그룹에서 삭제합니다.")
    public String inviteWithdrawal(@LoginUser LoginUserInfo loginUserInfo, @RequestBody InviteAcceptWitdrawlRequest inviteAcceptWitdrawlRequest) {
        return notificationWriteService.inviteWithdrawal(loginUserInfo.accountId(), inviteAcceptWitdrawlRequest);
    }
}
