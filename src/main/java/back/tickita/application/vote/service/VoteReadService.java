package back.tickita.application.vote.service;

import back.tickita.TickitaApplication;
import back.tickita.application.vote.dto.response.*;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.crews.repository.CrewListRepository;
import back.tickita.domain.crews.repository.CrewsRepository;
import back.tickita.domain.schedule.entity.Participant;
import back.tickita.domain.schedule.entity.Schedule;
import back.tickita.domain.schedule.repository.ScheduleRepository;
import back.tickita.domain.vote.entity.VoteList;
import back.tickita.domain.vote.entity.VoteState;
import back.tickita.domain.vote.entity.VoteSubject;
import back.tickita.domain.vote.enums.VoteEndType;
import back.tickita.domain.vote.enums.VoteType;
import back.tickita.domain.vote.repository.VoteListRepository;
import back.tickita.domain.vote.repository.VoteStateRepository;
import back.tickita.domain.vote.repository.VoteSubjectRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public VoteStateResponse findVoteState(Long accountId, Long crewId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (!crewListRepository.existsByAccountIdAndCrewsId(account.getId(), crewId)) {
            throw new TickitaException(ErrorCode.CREW_NOT_FOUND);
        }

        Crews crews = crewsRepository.findById(crewId).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));
        VoteSubject voteSubject = voteSubjectRepository.findByCrewsId(crewId).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));

        VoteList creatorVote = voteListRepository.findByVoteSubjectIdAndVoteType(voteSubject.getId(), VoteType.CREATOR)
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));

        List<VoteList> participant = voteListRepository.findAllByVoteSubjectIdAndVoteType(voteSubject.getId(), VoteType.PARTICIPANT);
        List<VoteListResponse> voteListResponses = participant.stream()
                .map(it -> new VoteListResponse(it.getCrewList().getAccount().getId(), it.getCrewList().getAccount().getNickName(), false))
                .collect(Collectors.toList());

        List<VoteState> voteDates = voteStateRepository.findByVoteSubjectId(voteSubject.getId());

        List<VoteDateListResponse> voteDateListResponses = voteDates.stream()
                .map(it -> new VoteDateListResponse(it.getScheduleDate(), it.getScheduleStartTime(), it.getScheduleEndTime()))
                .collect(Collectors.toList());

        VoteState voteState = voteStateRepository.findById(voteSubject.getId()).orElse(null);
        ;

        String remainTime = voteSubject.getRemainTime();

        return new VoteStateResponse(voteSubject.getTitle(), voteSubject.getContent(), voteSubject.getPlace(), crews.getId(), crews.getCrewName(),
                creatorVote.getCrewList().getAccount().getId(), voteListResponses, voteSubject.getEndTime(), voteSubject.getEndDate(), voteDateListResponses, voteState.getVoteCount(), remainTime);
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

    public List<VoteMypageResponse> findMypageVote(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        List<VoteMypageResponse> results = new ArrayList<>();
        List<CrewList> crewLists = crewListRepository.findAllByAccountId(account.getId());
        for (CrewList crewList : crewLists) {
            List<VoteList> voteList = crewList.getVoteList();
            for (VoteList voteParticipant : voteList) {
                Long voteSubjectId = voteParticipant.getVoteSubject().getId();
                if (voteParticipant.getVoteEndType() != null && voteParticipant.getVoteEndType() == VoteEndType.PROGRESS) {
                    VoteList voteCreator = voteListRepository.findByVoteSubjectIdAndVoteTypeFetchJoin(voteSubjectId, VoteType.CREATOR).orElse(null);
                    Long voteCreatorId = null;
                    String voteCreatorName = null;
                    if (voteCreator != null) {
                        voteCreatorName = voteCreator.getParticipateName();
                        voteCreatorId = voteCreator.getId();
                    }
                    results.add(new VoteMypageResponse(crewList.getCrews().getId(), voteParticipant.getVoteSubject().getTitle(), voteCreatorId, voteCreatorName, voteParticipant.getVoteSubject().getEndTime(),
                            voteParticipant.getVoteSubject().getEndDate(), voteParticipant.getVoteParticipateType()));
                }
            }
        }
        return results;
    }
}