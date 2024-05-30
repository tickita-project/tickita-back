package back.tickita.domain.token.repository;

import back.tickita.domain.token.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findByAccountId(Long accountId);

    Boolean existsByAccess(String access);
}
