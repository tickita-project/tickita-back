package back.tickita.domain.notification.entity;

import back.tickita.common.BaseEntity;
import back.tickita.domain.schedule.entity.Schedule;
import back.tickita.domain.vote.entity.VoteSubject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoordinationNotification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    private VoteSubject voteSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    public CoordinationNotification(Notification notification, Schedule schedule) {
        this.notification = notification;
        this.schedule = schedule;
    }

    public CoordinationNotification(Notification notification, VoteSubject voteSubject) {
        this.notification = notification;
        this.voteSubject = voteSubject;
    }
}