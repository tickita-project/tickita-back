package back.tickita.domain.schedule.repository;

import back.tickita.domain.schedule.entity.Participant;
import back.tickita.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.crews.id IN :crewId AND s IN (SELECT p.schedule FROM Participant p WHERE p.account.id = :accountId)")
    List<Schedule> findSchedulesByCrewIdAndParticipantId(@Param("crewId") Long crewId, @Param("accountId") Long accountId);

    @Query("SELECT p FROM Participant  p JOIN FETCH p.schedule s JOIN FETCH p.account a " +
            "WHERE :scheduleDate BETWEEN FUNCTION('DATE', s.startDateTime) AND  FUNCTION('DATE', s.endDateTime) AND a.id = :accountId")
    List<Participant> findAllSchedule(@Param("scheduleDate") LocalDate scheduleDate, @Param("accountId") Long accountId);
}


