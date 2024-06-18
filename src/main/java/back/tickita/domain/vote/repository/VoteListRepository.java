package back.tickita.domain.vote.repository;


import back.tickita.domain.vote.entity.VoteList;
import back.tickita.domain.vote.enums.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteListRepository extends JpaRepository<VoteList, Long> {
    Optional<VoteList> findByVoteSubjectIdAndVoteType(Long voteSubjectId, VoteType voteType);

    List<VoteList> findAllByVoteSubjectIdAndVoteType(Long voteSubjectId, VoteType voteType);

    Optional<VoteList> findByCrewListIdAndVoteTypeAndVoteSubjectId(Long crewListId, VoteType voteType, Long voteSubjectId);

    @Query(value = """
            SELECT count(*) as voteParticipateCount, vsj.id as voteSubjectId FROM vote_list as vl
             INNER JOIN vote_subject as vsj on vl.vote_subject_id = vsj.id
              WHERE vsj.vote_end_type = 'PROGRESS' AND vsj.end_date != :endDate AND vsj.end_time != :endTime GROUP BY vl.vote_subject_id
            """, nativeQuery = true)
    List<VoteCount> countByVoteSubject(@Param("endDate") LocalDate endDate, @Param("endTime") LocalTime endTime);

    List<VoteList> findAllByVoteSubjectId(Long voteSubjectId);

    @Query("""
                    SELECT vl FROM VoteList vl JOIN FETCH vl.crewList cl JOIN FETCH cl.account WHERE vl.voteSubject.id = :voteSubjectId AND vl.voteType = :voteType 
            """)
    Optional<VoteList> findByVoteSubjectIdAndVoteTypeFetchJoin(@Param("voteSubjectId") Long voteSubjectId, @Param("voteType") VoteType voteType);
}
