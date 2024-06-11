package back.tickita.application.notification.service;

import back.tickita.application.notification.dto.request.NotificationRequest;
import back.tickita.application.notification.dto.response.InviteNotificationResponse;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.enums.CrewAccept;
import back.tickita.domain.crews.repository.CrewListRepository;
import back.tickita.domain.notification.entity.Notification;
import back.tickita.domain.notification.repository.NotificationRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationWriteService {

    private final AccountRepository accountRepository;
    private final CrewListRepository crewListRepository;
    private final NotificationRepository notificationRepository;


    public InviteNotificationResponse setInviteAccept(Long accountId, NotificationRequest notificationRequest){
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        CrewList crewList = crewListRepository.findByAccount_Id(account.getId()).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));
        Notification notification = notificationRepository.findById(notificationRequest.getNotificationId()).orElseThrow(() -> new TickitaException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.update(true);

        if (notificationRequest.getInviteType() == CrewAccept.ACCEPT){
            crewList.setCrewAccept(notificationRequest.getInviteType());
        }else if (notificationRequest.getInviteType() == CrewAccept.DECLINE){
            crewList.setCrewAccept(notificationRequest.getInviteType());
        }

        return new InviteNotificationResponse(account.getId(), crewList.getCrews().getId(), crewList.getCrewAccept());
    }
}