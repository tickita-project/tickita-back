package back.tickita.domain.vote.repository;

import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.vote.entity.VoteSubject;
import back.tickita.domain.vote.enums.VoteEndType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteSubjectRepository extends JpaRepository<VoteSubject, Long> {

    Optional<VoteSubject> findByCrewsId(Long crewId);
    //base 현재 기준 =
    //database -> DATE_FORMAT(date_column, '%Y-%m-%d')
    // 현재 시간은 date_format(now(),'%H:%i:%s')
    //select * from vote_subject where vote_end_type = VoteEndType and end_date = DATE_FORMAT(now(), '%Y-%m-%d') and end_time = date_format(now(),'%H:%i:%s')
    List<VoteSubject> findAllByVoteEndTypeAndEndDateAndEndTime(VoteEndType voteEndType, LocalDate date, LocalTime time);

    @Modifying
    @Query("UPDATE VoteSubject SET voteEndType = 'FINISH' WHERE id = :voteSubjectId")
    void updateVoteEndType(@Param("voteSubjectId") Long voteSubjectId);

}
