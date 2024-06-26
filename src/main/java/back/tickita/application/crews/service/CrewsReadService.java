package back.tickita.application.crews.service;


import back.tickita.application.crews.dto.response.*;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.crews.enums.CrewAccept;
import back.tickita.domain.crews.enums.CrewRole;
import back.tickita.domain.crews.repository.CrewListRepository;
import back.tickita.domain.crews.repository.CrewsRepository;
import back.tickita.domain.notification.entity.CrewNotification;
import back.tickita.domain.notification.entity.Notification;
import back.tickita.domain.notification.repository.CrewNotificationRepository;
import back.tickita.domain.notification.repository.NotificationRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewsReadService {

    private final AccountRepository accountRepository;
    private final CrewListRepository crewListRepository;
    private final NotificationRepository notificationRepository;
    private final CrewNotificationRepository crewNotificationRepository;

    public CrewInfoResponse getCrewInfo(Long accountId, Long crewId) {
        CrewList crewList = crewListRepository.findByAccountIdAndCrewsId(accountId, crewId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));

        Crews crews = crewList.getCrews();

        List<CrewMemberInfoResponse> crewMemberInfos = crews.getCrewLists()
                .stream()
                .map(crewInfoList -> new CrewMemberInfoResponse(
                        crewInfoList.getCrewRole().name(), crewInfoList.getAccount().getId(), crewInfoList.getAccount().getNickName(), crewInfoList.getAccount().getEmail(), crewInfoList.getAccount().getImage()))
                .sorted(Comparator.comparingInt(crewInfo -> CrewRole.valueOf(crewInfo.getRole()).getOrder()))
                .collect(Collectors.toList());

        return new CrewInfoResponse(crews.getCrewName(), crews.getLabelColor(), crews.getId(), crewMemberInfos);
    }

    public CrewDetailResponse getCrewDetails(Long accountId, Long crewId) {
        CrewList crewList = crewListRepository.findByAccountIdAndCrewsId(accountId, crewId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));

        Crews crews = crewList.getCrews();

        if (crewList.getCrewAccept() != CrewAccept.ACCEPT) {
            throw new TickitaException(ErrorCode.FORBIDDEN_ACCESS);
        }

        List<CrewMemberInfoResponse> crewMemberInfos = crews.getCrewLists()
                .stream()
                .filter(crewInfoList -> crewInfoList.getCrewAccept() == CrewAccept.ACCEPT)
                .map(crewInfoList -> new CrewMemberInfoResponse(
                        crewInfoList.getCrewRole().name(), crewInfoList.getAccount().getId(), crewInfoList.getAccount().getNickName(), crewInfoList.getAccount().getEmail(), crewInfoList.getAccount().getImage()))
                .sorted(Comparator.comparingInt(crewInfo -> CrewRole.valueOf(crewInfo.getRole()).getOrder()))
                .collect(Collectors.toList());

        List<CrewWaitingMemberInfo> waitMembers = crews.getCrewLists()
                .stream()
                .filter(crewInfoList -> crewInfoList.getCrewAccept() == CrewAccept.WAIT)
                .map(crewInfoList -> {
                    CrewNotification crewNotification = crewNotificationRepository.findByCrewList(crewInfoList).orElseThrow(() -> new TickitaException(ErrorCode.NOTIFICATION_NOT_FOUND));
                    return new CrewWaitingMemberInfo(
                            crewNotification.getNotification().getId(), crewInfoList.getAccount().getId(), crewInfoList.getAccount().getNickName(), crewInfoList.getAccount().getEmail());
                }).collect(Collectors.toList());

        return new CrewDetailResponse(crews.getCrewName(), crewList.getIndividualColor(), crews.getId(), crewMemberInfos, waitMembers);
    }

    public CrewAllResponse getCrewAll(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        List<CrewList> crewList = crewListRepository.findAllByAccountId(account.getId());

        List<CrewAllInfo> crewLists = crewList
                .stream()
                .filter(crewAll -> CrewAccept.ACCEPT.equals(crewAll.getCrewAccept()))
                .map(crewAll -> new CrewAllInfo(
                        crewAll.getCrews().getId(), crewAll.getCrews().getCrewName(), crewAll.getIndividualColor()))
                .collect(Collectors.toList());

        return new CrewAllResponse(crewLists);
    }
}