package back.tickita.domain.notification.repository;

import back.tickita.domain.notification.entity.CoordinationNotification;
import back.tickita.domain.notification.eums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CoordinationNotificationRepository extends JpaRepository<CoordinationNotification, Long> {
    List<CoordinationNotification> findAllByVoteSubjectIdAndNotification_NotificationType(Long voteSubjectId, NotificationType notificationType);


    @Query(value = "SELECT * FROM coordination_notification cn " +
            "JOIN vote_subject vs ON cn.vote_subject_id = vs.id " +
            "WHERE vs.end_date < :time", nativeQuery = true)
    List<CoordinationNotification> findAllExpiredNotifications(LocalDateTime time);
}
