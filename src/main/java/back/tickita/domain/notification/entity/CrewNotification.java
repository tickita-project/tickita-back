package back.tickita.domain.notification.entity;

import back.tickita.common.BaseEntity;
import back.tickita.domain.crews.entity.CrewList;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CrewNotification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    private CrewList crewList;
}
