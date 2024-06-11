package back.tickita.domain.crews.entity;


import back.tickita.common.BaseEntity;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.crews.enums.CrewAccept;
import back.tickita.domain.crews.enums.CrewRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CrewList extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Crews crews;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Enumerated(EnumType.STRING)
    private CrewRole crewRole;

    @Enumerated(EnumType.STRING)
    private CrewAccept crewAccept;

    public void setCrewAccept(CrewAccept crewAccept) {
        this.crewAccept = crewAccept;
    }
}
