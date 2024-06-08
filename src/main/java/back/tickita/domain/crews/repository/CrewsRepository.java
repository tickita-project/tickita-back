package back.tickita.domain.crews.repository;

import back.tickita.domain.crews.entity.Crews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewsRepository extends JpaRepository<Crews, Long> {
}
