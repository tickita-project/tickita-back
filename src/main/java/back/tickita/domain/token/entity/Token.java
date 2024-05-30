package back.tickita.domain.token.entity;

import back.tickita.common.BaseEntity;
import back.tickita.domain.account.entity.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Token extends BaseEntity {
    private String refresh;
    private String access;
    private Date refreshExpiredAt;
    private Date accessExpiredAt;
    @OneToOne(fetch = FetchType.LAZY)
    private Account account;
}
