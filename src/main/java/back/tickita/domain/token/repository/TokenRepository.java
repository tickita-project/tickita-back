package back.tickita.domain.token.repository;

import back.tickita.domain.token.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findByAccountId(Long accountId);

    Boolean existsByAccess(String access);

    @Query("SELECT t FROM Token t JOIN FETCH t.account WHERE t.access = :access")
    Optional<Token> findTokenLoginInfo(@Param("access") String access);

    @Query("SELECT t FROM Token t JOIN FETCH t.account WHERE t.refresh = :refreshToken")
    Optional<Token> findByRefreshToken(@Param("refreshToken") String refreshToken);
}