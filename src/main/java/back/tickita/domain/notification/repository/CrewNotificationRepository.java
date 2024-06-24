package back.tickita.domain.notification.repository;

import back.tickita.domain.account.entity.Account;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.notification.entity.CrewNotification;
import back.tickita.domain.notification.entity.Notification;
import back.tickita.domain.notification.eums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrewNotificationRepository extends JpaRepository<CrewNotification, Long> {
    List<CrewNotification> findAllByCrewListInAndNotification_NotificationType(List<CrewList> crewList, NotificationType notificationType);

    Optional<CrewNotification> findByCrewList(CrewList crewList);
}
