package back.tickita.domain.vote.entity;

import back.tickita.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteState extends BaseEntity {

    private LocalDate scheduleDate;

    private LocalTime scheduleStartTime;

    private LocalTime scheduleEndTime;

    private int voteCount;

    @ManyToOne(fetch = FetchType.LAZY)
    private VoteSubject voteSubject;

    public static VoteState create(LocalDate scheduleDate, LocalTime scheduleStartTime, LocalTime scheduleEndTime, VoteSubject voteSubject){
        return new VoteState(scheduleDate, scheduleStartTime, scheduleEndTime, 1, voteSubject);
    }

    public void updateCount() {
        this.voteCount += 1;
    }
}
