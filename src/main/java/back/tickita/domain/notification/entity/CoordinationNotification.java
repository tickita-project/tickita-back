package back.tickita.domain.notification.entity;

import back.tickita.common.BaseEntity;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.notification.eums.CoordinationType;
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
}