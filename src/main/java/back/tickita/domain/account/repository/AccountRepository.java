package back.tickita.domain.account.repository;

import back.tickita.application.account.dto.request.LoginUserInfo;
import back.tickita.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);;
}
