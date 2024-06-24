package back.tickita.application.vote.service;


import back.tickita.application.vote.dto.request.VoteStateRequest;
import back.tickita.application.vote.dto.request.VoteSubjectRequest;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.account.repository.log.ErrorLog;
import back.tickita.domain.account.repository.log.ErrorLogRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.crews.repository.CrewListRepository;
import back.tickita.domain.crews.repository.CrewsRepository;
import back.tickita.domain.notification.entity.CoordinationNotification;
import back.tickita.domain.notification.entity.Notification;
import back.tickita.domain.notification.eums.NotificationType;
import back.tickita.domain.notification.repository.CoordinationNotificationRepository;
import back.tickita.domain.notification.repository.NotificationRepository;
import back.tickita.domain.schedule.entity.Participant;
import back.tickita.domain.schedule.entity.Schedule;
import back.tickita.domain.schedule.repository.ParticipantRepository;
import back.tickita.domain.schedule.repository.ScheduleRepository;
import back.tickita.domain.vote.entity.VoteComplete;
import back.tickita.domain.vote.entity.VoteList;
import back.tickita.domain.vote.entity.VoteState;
import back.tickita.domain.vote.entity.VoteSubject;
import back.tickita.domain.vote.enums.VoteEndType;
import back.tickita.domain.vote.enums.VoteType;
import back.tickita.domain.vote.repository.*;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteWriteService {

    private final AccountRepository accountRepository;
    private final CrewListRepository crewListRepository;
    private final VoteSubjectRepository voteSubjectRepository;
    private final VoteListRepository voteListRepository;
    private final CrewsRepository crewsRepository;
    private final VoteStateRepository voteStateRepository;
    private final NotificationRepository notificationRepository;
    private final CoordinationNotificationRepository coordinationNotificationRepository;
    private final VoteCompleteRepository voteCompleteRepository;
    private final ScheduleRepository scheduleRepository;
    private final ErrorLogRepository errorLogRepository;
    private final HttpServletRequest httpServletRequest;
    private final ParticipantRepository participantRepository;

    public void create(Long accountId, VoteSubjectRequest voteSubjectRequest) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (!crewListRepository.existsByAccountIdAndCrewsId(account.getId(), voteSubjectRequest.crewId())) {
            throw new TickitaException(ErrorCode.CREW_NOT_FOUND);
        }

        Crews crews = crewsRepository.findById(voteSubjectRequest.crewId()).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));

        VoteSubject voteSubject = VoteSubject.create(voteSubjectRequest.title(), voteSubjectRequest.content(), voteSubjectRequest.place(), voteSubjectRequest.endTime(), voteSubjectRequest.endDate(), crews);
        VoteSubject savedVoteSubject = voteSubjectRepository.save(voteSubject);
        CrewList creatorCrewList = crewListRepository.findByAccountIdAndCrewsId(account.getId(), voteSubjectRequest.crewId()).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));
        VoteList creator = VoteList.creator(creatorCrewList, voteSubject);

        List<VoteList> voteContainer = new ArrayList<>();


        voteContainer.add(creator);
        VoteSubject findVoteSubject = voteSubjectRepository.findById(voteSubject.getId()).orElse(null);
        List<VoteList> voteResults = voteSubjectRequest.accountIds().stream().map(it -> {
            CrewList crewList = crewListRepository.findByAccountIdAndCrewsId(it, voteSubjectRequest.crewId()).orElse(null);

            if (crewList == null) {
                throw new TickitaException(ErrorCode.CREW_NOT_FOUND);
            }
            Notification savedNotification = notificationRepository.save(new Notification(NotificationType.REQUEST, crewList.getAccount()));
            coordinationNotificationRepository.save(new CoordinationNotification(savedNotification, findVoteSubject));
            return VoteList.participant(crewList, voteSubject);
        }).collect(Collectors.toList());

        voteContainer.addAll(voteResults);
        voteListRepository.saveAll(voteContainer);
        voteSubjectRequest.voteDateLists().forEach(it -> {
            VoteState voteState = VoteState.create(it.getVoteDate(), it.getVoteStartTime(), it.getVoteEndTime(), savedVoteSubject);
            VoteState savedVoteState = voteStateRepository.save(voteState);
            VoteComplete voteComplete = new VoteComplete(creator, savedVoteState);
            voteCompleteRepository.save(voteComplete);
        });

    }

    public void setVote(Long accountId, VoteStateRequest voteStateRequest, Long voteSubjectId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND)); // 투표하는 회원
        CrewList crewList = crewListRepository.findByAccountIdAndCrewsId(accountId, voteStateRequest.getCrewId()).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND)); // 회원이 크루 반환

        VoteSubject voteSubject = voteSubjectRepository.findById(voteSubjectId).orElseThrow(() -> new TickitaException(ErrorCode.VOTE_SUBJECT_NOT_FOUND)); // 투표 찾기

        VoteList voteList = voteListRepository.findByCrewListIdAndVoteTypeAndVoteSubjectId(crewList.getId(),
                VoteType.PARTICIPANT, voteSubject.getId()).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));  //투표에 참석된 인원 찾기
        List<VoteState> voteStates = voteStateRepository.findAllByIdIn(voteStateRequest.getVoteStateIds());
        for (VoteState voteState : voteStates) {
            voteState.updateCount();
        }

        List<VoteComplete> voteCompletes = voteStates.stream().map(voteState -> new VoteComplete(voteList, voteState)).collect(Collectors.toList()); // 투표 참석자 내역 저장
        voteCompleteRepository.saveAll(voteCompletes);
        voteList.setVoteType(true);

        CoordinationNotification coordinationNotification = coordinationNotificationRepository.findByVoteSubjectId(voteSubject.getId()).orElseThrow(() -> new TickitaException(ErrorCode.NOTIFICATION_NOT_FOUND));
        Notification notification = coordinationNotification.getNotification();
        notification.update(true);

        Integer voteCompleteCount = voteCompleteRepository.countVoteComplete(voteSubjectId);
        long voteParticipateCount = voteListRepository.countByVoteSubjectId(voteSubjectId);
        if (voteCompleteCount != null && voteCompleteCount == voteParticipateCount) {
            voteSubjectRepository.updateVoteEndType(voteSubjectId);
            createSchedule(voteSubject);
        }
    }

    //투표 동률일 때 -> 가장 빠른 날짜와 시간대 리스트로 적용 -> 일정 테이블에 자동 저장
    //마감이 되지 않았어도 투표를 다 했으면 -> 일정 테이블에 자동 저장
    //마감시간이 종료되었을 시 가장 높은 count로 일정 테이블에 자동 저장
    //여기서 마감시간이 되었는데 투표하지 않은 참석자는 일정에서 제외 처리

    /**
     * VoteSubject -> 투표 주제
     * VoteState -> 투표 내용
     * VoteList -> 참여 인원
     * VoteComplete -> 투표 완료
     * 투표 마감된 거 -> 스케줄러를 1초씩돌리고 현재시간 ~ -1초 시간까지 투표 마감시간이거 찾아서 -> 상태값 변경 -> 스케줄러 따로
     * 또는 투표 다 한거 다 찾기
     */


    // 스케줄러를 1초씩돌리고 현재시간 = 마감 시간이랑 동일하면 -> 상태값 변경 -> 스케줄러 따로
    // 현재시간 기준으로 만약에 시간이랑 날짝 같으면 상태값 변경
    public void updateExpireVoteStatus(LocalDate nowDate, LocalTime nowTime) {
        // 마감 상태가 아닌데 마감시간인것들 마감으로 변환
        List<VoteSubject> voteSubjects = voteSubjectRepository.findAllByVoteEndTypeAndEndDateAndEndTime(VoteEndType.PROGRESS, nowDate, nowTime);
        for (VoteSubject voteSubject : voteSubjects) {
            voteSubject.update(); // change status progress -> finish
            createSchedule(voteSubject);
        }

        //마감 되지 않아도 투표가 완료되면 변경
        List<VoteCount> voteCounts = voteListRepository.countByVoteSubject(nowDate, nowTime); // 전체 투표 별 참여 인원수 추출
        for (VoteCount voteCount : voteCounts) {
            Integer voteCompleteCount = voteCompleteRepository.countVoteComplete(voteCount.getVoteSubjectId());

            if (voteCompleteCount != null && voteCompleteCount == voteCount.getVoteParticipateCount()) {
                voteSubjectRepository.updateVoteEndType(voteCount.getVoteSubjectId());
                VoteSubject voteSubject = voteSubjectRepository.findById(voteCount.getVoteSubjectId()).orElse(null);
                if (voteSubject == null) {
                    continue;
                }
                createSchedule(voteSubject);
            }
        }
    }

    private void createSchedule(VoteSubject voteSubject) {
        VoteState voteState = voteStateRepository.findTop1VoteState(voteSubject.getId()).orElse(null);
        if (voteState == null) {
            errorLogRepository.save(ErrorLog.create(400, "Schedule", HttpStatus.BAD_REQUEST.name(), "일정 현황이 존재하지 않습니다.", httpServletRequest.getRequestURI()));
            return;
        }
        LocalDateTime startDateTime = LocalDateTime.of(voteState.getScheduleDate().getYear(), voteState.getScheduleDate().getMonth(), voteState.getScheduleDate().getDayOfMonth(),
                voteState.getScheduleStartTime().getHour(), voteState.getScheduleStartTime().getMinute(), voteState.getScheduleStartTime().getSecond());
        LocalDateTime endDateTime = LocalDateTime.of(voteState.getScheduleDate().getYear(), voteState.getScheduleDate().getMonth(), voteState.getScheduleDate().getDayOfMonth(),
                voteState.getScheduleEndTime().getHour(), voteState.getScheduleEndTime().getMinute(), voteState.getScheduleEndTime().getSecond());

        VoteList creatorVote = voteListRepository.findByVoteSubjectIdAndVoteType(voteSubject.getId(), VoteType.CREATOR)
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        Long creatorId = creatorVote.getCrewList().getAccount().getId();

        Schedule savedSchedule = scheduleRepository.save(
                new Schedule(voteSubject.getTitle(), startDateTime, endDateTime, voteSubject.getPlace(), voteSubject.getContent(), voteSubject.getCrews(), creatorId, true)
        );
        List<VoteList> voteLists = voteListRepository.findAllByVoteSubjectId(voteSubject.getId());
        for (VoteList voteList : voteLists) {
            int voteListCompleteSize = voteList.getComplete().size();
            Account account = voteList.getAccount();
            if (voteListCompleteSize == 0 || account == null) {
                Notification savedNotification = notificationRepository.save(new Notification(NotificationType.EXCLUDE, account));
                CoordinationNotification coordinationNotification = new CoordinationNotification(savedNotification, voteSubject, savedSchedule);
                coordinationNotificationRepository.save(coordinationNotification);
                continue;
            }
            Notification savedNotification = notificationRepository.save(new Notification(NotificationType.SCHEDULE_INFO, account));
            CoordinationNotification coordinationNotification = new CoordinationNotification(savedNotification, voteSubject, savedSchedule);
            coordinationNotificationRepository.save(coordinationNotification);
            participantRepository.save(new Participant(account, savedSchedule));
        }
    }
}
