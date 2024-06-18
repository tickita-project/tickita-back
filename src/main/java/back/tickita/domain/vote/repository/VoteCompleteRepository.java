package back.tickita.domain.vote.repository;

import back.tickita.domain.vote.entity.VoteComplete;
import back.tickita.domain.vote.entity.VoteList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteCompleteRepository extends JpaRepository<VoteComplete, Long> {
    @Query(value = """
            SELECT count(distinct vc.vote_list_id) FROM vote_complete as vc inner join vote_state as vs on vs.id = vc.vote_state_id
                          where vs.vote_subject_id = :voteSubjectId;             
            """, nativeQuery = true)
    Integer countVoteComplete(@Param("voteSubjectId") Long voteSubjectId);


    List<VoteComplete> findAllByVoteListIn(List<VoteList> voteLists);
}