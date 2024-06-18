package back.tickita.domain.notification.repository;

import back.tickita.domain.notification.entity.CoordinationNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordinationNotificationRepository extends JpaRepository<CoordinationNotification, Long> {
}
