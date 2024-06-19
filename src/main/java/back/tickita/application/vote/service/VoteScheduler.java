package back.tickita.application.vote.service;

import back.tickita.domain.vote.entity.VoteSubject;
import back.tickita.domain.vote.repository.VoteSubjectRepository;
import lombok.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Component
public class VoteScheduler {
    private final VoteWriteService voteWriteService;
    private final VoteSubjectRepository voteSubjectRepository;
    private final VoteReadService voteReadService;

    @Scheduled(cron = "*/1 * * * * *")
    public void checkAndSaveFinalSchedule() {
        voteWriteService.updateExpireVoteStatus(LocalDate.now(), LocalTime.now());
        voteReadService.processExpiredNotifications(LocalDateTime.now());
    }
}
