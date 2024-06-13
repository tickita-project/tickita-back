package back.tickita.domain.notification.entity;

import back.tickita.common.BaseEntity;
import back.tickita.domain.notification.eums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private Boolean isChecked = false;

    @OneToMany(mappedBy = "notification", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CrewNotification> crewNotifications;

    public Notification(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public void update(Boolean isChecked) {
        this.isChecked = isChecked;
    }
}
