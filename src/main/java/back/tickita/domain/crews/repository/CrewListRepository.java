package back.tickita.domain.crews.repository;

import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrewListRepository extends JpaRepository<CrewList, Long> {
    List<CrewList> findByAccountId(Long accountId);
    Optional<CrewList> findByAccountIdAndCrewsId(Long accountId, Long crewId);
}
