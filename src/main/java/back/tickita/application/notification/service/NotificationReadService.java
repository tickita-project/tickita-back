package back.tickita.application.notification.service;

import back.tickita.application.notification.dto.response.NotificationInfo;
import back.tickita.application.notification.dto.response.NotificationResponse;
import back.tickita.application.notification.dto.response.ScheduleInfo;
import back.tickita.application.notification.dto.response.enums.AlarmType;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.enums.CrewAccept;
import back.tickita.domain.crews.repository.CrewListRepository;
import back.tickita.domain.notification.entity.CoordinationNotification;
import back.tickita.domain.notification.entity.CrewNotification;
import back.tickita.domain.notification.entity.Notification;
import back.tickita.domain.notification.eums.NotificationType;
import back.tickita.domain.notification.repository.CrewNotificationRepository;
import back.tickita.domain.notification.repository.NotificationRepository;
import back.tickita.domain.schedule.entity.Schedule;
import back.tickita.domain.schedule.repository.ScheduleRepository;
import back.tickita.domain.vote.entity.VoteSubject;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationReadService {

    private final AccountRepository accountRepository;
    private final CrewListRepository crewListRepository;
    private final CrewNotificationRepository crewNotificationRepository;
    private final NotificationRepository notificationRepository;


    public NotificationResponse findAllNotification(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        List<NotificationInfo> results = new ArrayList<>();
        List<NotificationInfo> alarmCrewType = getAlarmCrewType(accountId);
        results.addAll(alarmCrewType);
        results.addAll(getAlarmScheduleType(accountId));
//        results = results.stream().filter(result -> !result.getIsChecked()).collect(Collectors.toList());
        results.sort(Comparator.comparing(NotificationInfo::getLocalDateTime));
        long count = results.stream().filter(it -> !it.getIsChecked()).count();
        return new NotificationResponse(count, results);
    }

    private List<NotificationInfo> getAlarmCrewType(Long accountId) {
        List<CrewList> crewList = crewListRepository.findAllByAccountId(accountId);
        List<CrewNotification> crewNotifications = crewNotificationRepository.findAllByCrewListInAndNotification_NotificationType(crewList, NotificationType.INVITE)
                .stream()
                .filter(crewNotification -> crewNotification.getCrewList().getCrewAccept() == CrewAccept.WAIT)
                .collect(Collectors.toList());

        return crewNotifications.stream()
                .map(crewNotification -> new NotificationInfo(crewNotification.getId(), crewNotification.getNotification().getNotificationType().name(),
                        crewNotification.getCrewList().getCrews().getId(), crewNotification.getCrewList().getIndividualColor(),
                        crewNotification.getCrewList().getCrews().getCrewName(), null, crewNotification.getCreatedAt(), crewNotification.getNotification().getIsChecked(),
                        crewNotification.getNotification().getNotificationType().getContent(), AlarmType.CREW))
                .collect(Collectors.toList());
    }

    private List<NotificationInfo> getAlarmScheduleType(Long accountId) {
        //Schedule
        List<Notification> notificationList = notificationRepository.findAllByAccountId(accountId);
        List<NotificationInfo> notificationInfos = new ArrayList<>();
        for (Notification notification : notificationList) {
            List<CoordinationNotification> coordinationNotifications = notification.getCoordinationNotifications();
            for (CoordinationNotification coordinationNotification : coordinationNotifications) {
                VoteSubject voteSubject = coordinationNotification.getVoteSubject();
                NotificationInfo notificationInfo = null;
                if (voteSubject != null) {
                    String individualColor = getIndividualColor(accountId, voteSubject.getCrews().getId());
                    notificationInfo = new NotificationInfo(
                            notification.getId(), notification.getNotificationType().name(), voteSubject.getCrews().getId(),
                            individualColor, voteSubject.getCrews().getCrewName(),null ,
                            notification.getCreatedAt(), notification.getIsChecked(), notification.getNotificationType().getContent(),AlarmType.SCHEDULE
                    );
                    if(coordinationNotification.getSchedule() != null) {
                        notificationInfo.createScheduleInfo(new ScheduleInfo(coordinationNotification.getSchedule().getId(), coordinationNotification.getSchedule().getTitle()));
                    }
                }
                else if(coordinationNotification.getSchedule() != null) {
                    Schedule schedule = coordinationNotification.getSchedule();
                    String individualColor = getIndividualColor(accountId, schedule.getCrews().getId());
                    notificationInfo = new NotificationInfo(
                            notification.getId(), notification.getNotificationType().name(), schedule.getCrews().getId(),
                            individualColor, schedule.getCrews().getCrewName(),null ,
                            notification.getCreatedAt(), notification.getIsChecked(), notification.getNotificationType().getContent(),AlarmType.SCHEDULE
                    );
                }
                notificationInfos.add(notificationInfo);
            }
        }
        return notificationInfos;
    }

    private String getIndividualColor(Long accountId, Long crewId) {
        CrewList crewList = crewListRepository.findByAccountIdAndCrewsId(accountId, crewId)
                .orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));
        return crewList.getIndividualColor();
    }
}
