package back.tickita.domain.notification.repository;

import back.tickita.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = "SELECT n.* FROM notification n " +
            "JOIN crew_notification cn ON n.id = cn.notification_id " +
            "WHERE cn.crew_list_id = :crewListId", nativeQuery = true)
    Optional<Notification> findByCrewListId(Long crewListId);
}
