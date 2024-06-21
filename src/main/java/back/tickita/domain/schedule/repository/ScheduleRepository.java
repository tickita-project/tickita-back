package back.tickita.domain.schedule.repository;

import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.schedule.entity.Schedule;
import org.hibernate.sql.model.internal.OptionalTableUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findById(Long scheduleId);

    List<Schedule> findAllByCrews(Crews crews);

    List<Schedule> findTop10ByParticipants_Account_IdAndStartDateTimeAfterOrderByStartDateTime(Long accountId, LocalDateTime now);
}
