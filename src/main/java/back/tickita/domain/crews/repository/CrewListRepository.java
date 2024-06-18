package back.tickita.domain.crews.repository;

import back.tickita.domain.account.entity.Account;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.crews.enums.CrewRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrewListRepository extends JpaRepository<CrewList, Long> {
    List<CrewList> findAllByAccountId(Long accountId);
    Optional<CrewList> findByAccount_Id(Long accountId);
    Optional<CrewList> findByAccountIdAndCrewsId(Long accountId, Long crewId);

    Optional<CrewList> findByAccountAndCrews(Account account, Crews crews);
    List<CrewList> findAllByCrewsId(Long crewId);

    boolean existsByAccountIdAndCrewsId(Long accountId, Long crewId);

    Optional<CrewList> findByCrewsId(Long crewId);

    List<CrewList> findByCrews(Crews crews);
    boolean existsByCrewsIdAndAccountIdAndCrewRole(Long crewId, Long accountId, CrewRole role);
}
