package back.tickita.domain.token.entity;

import back.tickita.common.BaseEntity;
import back.tickita.domain.account.entity.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Token extends BaseEntity {
    private String access;
    private String refresh;
    private LocalDateTime accessExpiredAt;
    private LocalDateTime refreshExpiredAt;
    @OneToOne(fetch = FetchType.LAZY)
    private Account account;

    public Token(String access, LocalDateTime accessExpiredAt, Account account, String refresh, LocalDateTime refreshExpiredAt) {
        this.access = access;
        this.accessExpiredAt = accessExpiredAt;
        this.account = account;
        this.refresh = refresh;
        this.refreshExpiredAt = refreshExpiredAt;
    }

    public static Token create(String access, LocalDateTime accessExpiredAt, Account account, String refresh, LocalDateTime refreshExpiredAt) {
        return new Token(access, accessExpiredAt, account, refresh, refreshExpiredAt);
    }

    public void updateAccessToken(String access, LocalDateTime accessExpiredAt) {
        this.access = access;
        this.accessExpiredAt = accessExpiredAt;
    }
    public void updateRefreshToken(String refresh, LocalDateTime refreshExpiredAt) {
        this.refresh = refresh;
        this.refreshExpiredAt = refreshExpiredAt;
    }
    public Long returnAccountId() {
        return account != null ? account.getId() : null;
    }
}
