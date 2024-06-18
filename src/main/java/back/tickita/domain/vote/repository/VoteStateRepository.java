package back.tickita.domain.vote.repository;

import back.tickita.domain.vote.entity.VoteState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteStateRepository extends JpaRepository<VoteState, Long> {
    List<VoteState> findByVoteSubjectId(Long voteSubjectId);

    // List<Long> id -> 1, 2, 3,  4
    //select * from vote_state where id in (1,2,3,4)
    List<VoteState> findAllByIdIn(List<Long> voteStateIds);


    @Query(value = """
            SELECT vs.* FROM vote_state vs
             INNER JOIN vote_subject vsj ON vs.vote_subject_id = vsj.id
              WHERE vsj.id = :voteSubjectId ORDER BY vs.vote_count DESC, vs.schedule_date ASC, vs.schedule_start_time ASC, vs.schedule_end_time ASC LIMIT 1 
            """, nativeQuery = true)
    Optional<VoteState> findTop1VoteState(@Param("voteSubjectId") Long voteSubjectId);
}

