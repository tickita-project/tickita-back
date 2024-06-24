package back.tickita.domain.schedule.repository;

import back.tickita.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findById(Long scheduleId);

    @Query("SELECT s FROM Schedule s JOIN s.participants p WHERE p.account.id = :accountId " +
            "AND (s.startDateTime >= :currentDateTime OR (s.startDateTime < :currentDateTime AND s.startDateTime >= :startOfCurrentDay)) ORDER BY s.startDateTime")
    List<Schedule> findTop10ByAccountIdAndStartDateTimeAfterOrOnSameDayOrderByStartDateTime(
            @Param("accountId") Long accountId, @Param("currentDateTime") LocalDateTime currentDateTime,
            @Param("startOfCurrentDay") LocalDateTime startOfCurrentDay);
}
