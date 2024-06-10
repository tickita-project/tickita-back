package back.tickita.domain.schedule.repository;

import back.tickita.domain.schedule.entity.Participant;
import back.tickita.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.crews.id IN :crewIds AND s IN (SELECT p.schedule FROM Participant p WHERE p.account.id = :accountId)")
    List<Schedule> findSchedulesByCrewIdsAndParticipantId(@Param("crewIds") List<Long> crewIds, @Param("accountId") Long accountId);
}


