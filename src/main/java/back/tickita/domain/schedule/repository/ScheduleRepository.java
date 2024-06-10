package back.tickita.domain.schedule.repository;

import back.tickita.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findById(Long scheduleId); // 일정 id로 찾기

//    @Query("SELECT s FROM Schedule s JOIN s.participants p WHERE p.account.id = :accountId ORDER BY s.startTime ASC")
//    List<Schedule> findSchedulesByAccountIdOrderByStartTime(@Param("accountId") Long accountId); // 사용자별 모든 일정 startTime순으로 가져오기
}
