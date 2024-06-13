package back.tickita.application.notification.service;

import back.tickita.application.notification.dto.response.CrewNotificationResponse;
import back.tickita.application.notification.dto.response.NotificationResponse;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.enums.CrewAccept;
import back.tickita.domain.crews.repository.CrewListRepository;
import back.tickita.domain.notification.entity.CrewNotification;
import back.tickita.domain.notification.eums.NotificationType;
import back.tickita.domain.notification.repository.CrewNotificationRepository;
import back.tickita.domain.notification.repository.NotificationRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationReadService {

    private final AccountRepository accountRepository;
    private final CrewListRepository crewListRepository;
    private final CrewNotificationRepository crewNotificationRepository;


    public NotificationResponse findAllNotification(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        List<CrewList> crewList = crewListRepository.findAllByAccountId(account.getId());
        List<CrewNotification> crewNotifications = crewNotificationRepository.findAllByCrewListInAndNotification_NotificationType(crewList, NotificationType.INVITE)
                .stream()
                .filter(crewNotification -> crewNotification.getCrewList().getCrewAccept() == CrewAccept.WAIT)
                .collect(Collectors.toList());
        Long count = crewNotifications.stream().filter(crewNotification -> !crewNotification.getNotification().getIsChecked())
                .count();

        List<CrewNotificationResponse> response = crewNotifications.stream()
                .map(crewNotification -> new CrewNotificationResponse(crewNotification.getId(), crewNotification.getNotification().getNotificationType().name(), crewNotification.getCrewList().getCrews().getId(),
                        account.getId(), crewNotification.getCrewList().getCrews().getCrewName(), null, crewNotification.getCreatedAt(), false, null, crewNotification.getNotification().getNotificationType().getContent()))
                .collect(Collectors.toList());
        return new NotificationResponse(count, response);
    }
}
