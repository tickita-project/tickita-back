package back.tickita.application.vote.service;

import back.tickita.TickitaApplication;
import back.tickita.application.vote.dto.response.*;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.crews.repository.CrewListRepository;
import back.tickita.domain.crews.repository.CrewsRepository;
import back.tickita.domain.notification.entity.CoordinationNotification;
import back.tickita.domain.notification.entity.CrewNotification;
import back.tickita.domain.notification.entity.Notification;
import back.tickita.domain.notification.eums.NotificationType;
import back.tickita.domain.notification.repository.CoordinationNotificationRepository;
import back.tickita.domain.notification.repository.NotificationRepository;
import back.tickita.domain.schedule.entity.Participant;
import back.tickita.domain.schedule.entity.Schedule;
import back.tickita.domain.schedule.repository.ScheduleRepository;
import back.tickita.domain.vote.entity.VoteComplete;
import back.tickita.domain.vote.entity.VoteList;
import back.tickita.domain.vote.entity.VoteState;
import back.tickita.domain.vote.entity.VoteSubject;
import back.tickita.domain.vote.enums.VoteEndType;
import back.tickita.domain.vote.enums.VoteType;
import back.tickita.domain.vote.repository.VoteCompleteRepository;
import back.tickita.domain.vote.repository.VoteListRepository;
import back.tickita.domain.vote.repository.VoteStateRepository;
import back.tickita.domain.vote.repository.VoteSubjectRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteReadService {

    private final AccountRepository accountRepository;
    private final CrewListRepository crewListRepository;
    private final CrewsRepository crewsRepository;
    private final VoteSubjectRepository voteSubjectRepository;
    private final VoteListRepository voteListRepository;
    private final VoteStateRepository voteStateRepository;
    private final ScheduleRepository scheduleRepository;
    private final CoordinationNotificationRepository coordinationNotificationRepository;
    private final VoteCompleteRepository voteCompleteRepository;
    private final NotificationRepository notificationRepository;

    public VoteStateResponse findVoteState(Long accountId, Long crewId, Long voteSubjectId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (!crewListRepository.existsByAccountIdAndCrewsId(account.getId(), crewId)) {
            throw new TickitaException(ErrorCode.CREW_NOT_FOUND);
        }

        Crews crews = crewsRepository.findById(crewId).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));
        VoteSubject voteSubject = voteSubjectRepository.findByCrewsIdAndId(crews.getId(), voteSubjectId).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));

        VoteList creatorVote = voteListRepository.findByVoteSubjectIdAndVoteType(voteSubject.getId(), VoteType.CREATOR)
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));

        List<VoteList> participant = voteListRepository.findAllByVoteSubjectIdAndVoteType(voteSubject.getId(), VoteType.PARTICIPANT);
        List<VoteListResponse> voteListResponses = participant.stream()
                .map(it -> new VoteListResponse(it.getCrewList().getAccount().getId(), it.getCrewList().getAccount().getNickName(), false))
                .collect(Collectors.toList());

        List<VoteState> voteDates = voteStateRepository.findByVoteSubjectId(voteSubject.getId());

        List<VoteDateListResponse> voteDateListResponses = voteDates.stream()
                .map(it -> new VoteDateListResponse(it.getScheduleDate(), it.getScheduleStartTime(), it.getScheduleEndTime(), it.getVoteCount()))
                .collect(Collectors.toList());

        VoteState voteState = voteStateRepository.findById(voteSubject.getId()).orElse(null);
        Long remainTime = voteSubject.getRemainTime();

        return new VoteStateResponse(voteSubject.getTitle(), voteSubject.getContent(), voteSubject.getPlace(), crews.getId(), crews.getCrewName(), crews.getLabelColor(),
                creatorVote.getCrewList().getAccount().getId(), creatorVote.getCrewList().getAccount().getNickName(), voteListResponses, voteSubject.getEndTime(), voteSubject.getEndDate(), voteDateListResponses, remainTime);
    }

    public VoteParticipantTimeList findParticipantTime(Long crewId, Long voteSubjectId) {
        Crews crews = crewsRepository.findById(crewId).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));

        List<VoteList> voteLists = voteListRepository.findAllByVoteSubjectId(voteSubjectId);

        List<ParticipantTime> participantTimes = new ArrayList<>();
        for (VoteList voteList : voteLists) {
            CrewList crewList = voteList.getCrewList();
            List<Schedule> schedules = scheduleRepository.findAllByCrews(crews);

            for (Schedule schedule : schedules) {
                ParticipantTime participantTime = new ParticipantTime();
                participantTime.setParticipant(crewList.getAccount().getId(), schedule.getStartDateTime(), schedule.getEndDateTime());
                participantTimes.add(participantTime);
            }
        }
        VoteParticipantTimeList voteParticipantTimeList = new VoteParticipantTimeList();
        voteParticipantTimeList.setParticipantTimes(participantTimes);

        return voteParticipantTimeList;
    }

    public CoordinationNotificationResponse findMypageVoteNotification(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        List<VoteNotificationResponse> results = new ArrayList<>();
        List<CrewList> crewLists = crewListRepository.findAllByAccountId(account.getId());

        Long count = 0L;

        for (CrewList crewList : crewLists) {
            List<VoteList> voteList = crewList.getVoteList();
            for (VoteList voteParticipant : voteList) {
                Long voteSubjectId = voteParticipant.getVoteSubject().getId();
                if (voteParticipant.getVoteEndType() != null && voteParticipant.getVoteEndType() == VoteEndType.PROGRESS) {
                    List<CoordinationNotification> coordinationNotifications = coordinationNotificationRepository.findAllByVoteSubjectIdAndNotification_NotificationType(voteSubjectId, NotificationType.REQUEST);
                    for (CoordinationNotification coordinationNotification : coordinationNotifications) {
                        coordinationNotification.getNotification().update(true);
                        if (!coordinationNotification.getNotification().getIsChecked()){
                            count++;
                        }
                        VoteNotificationResponse voteNotificationResponse = new VoteNotificationResponse();
                        voteNotificationResponse.setVoteNotification(coordinationNotification.getNotification().getId(), coordinationNotification.getNotification().getNotificationType().name(),
                                crewList.getCrews().getId(), account.getId(), crewList.getCrews().getCrewName(), coordinationNotification.getCreatedAt(), coordinationNotification.getNotification().getIsChecked(),
                                voteSubjectId, coordinationNotification.getVoteSubject().getTitle(), coordinationNotification.getVoteSubject().getEndTime(), coordinationNotification.getVoteSubject().getEndDate(), voteParticipant.getVoteParticipateType());
                        results.add(voteNotificationResponse);
                    }
                }
            }
        }
        return new CoordinationNotificationResponse(count, results);
    }

    public void processExpiredNotifications(LocalDateTime time) {
        List<CoordinationNotification> expiredNotifications = coordinationNotificationRepository.findAllExpiredNotifications(time);

        for (CoordinationNotification notification : expiredNotifications) {
            notification.getNotification().update(true);
        }
    }
}