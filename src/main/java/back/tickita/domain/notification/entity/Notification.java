package back.tickita.domain.notification.entity;

import back.tickita.common.BaseEntity;
import back.tickita.domain.notification.eums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private Boolean isChecked = false;

    public Notification(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public void update(Boolean isChecked) {
        this.isChecked = isChecked;
    }
}
