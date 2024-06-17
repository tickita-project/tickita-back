package back.tickita.domain.crews.entity;


import back.tickita.common.BaseEntity;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.crews.enums.CrewAccept;
import back.tickita.domain.crews.enums.CrewRole;
import back.tickita.domain.notification.entity.CrewNotification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CrewList extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Crews crews;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Column(nullable = false)
    private String individualColor;

    @Enumerated(EnumType.STRING)
    private CrewRole crewRole;

    @Enumerated(EnumType.STRING)
    private CrewAccept crewAccept;

    @OneToMany(mappedBy = "crewList", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CrewNotification> crewNotifications;

    public CrewList(Crews crews, Account account, String individualColor, CrewRole crewRole, CrewAccept crewAccept) {
        this.crews = crews;
        this.account = account;
        this.individualColor = individualColor;
        this.crewRole = crewRole;
        this.crewAccept = crewAccept;
    }

    public void setCrewAccept(CrewAccept crewAccept) {
        this.crewAccept = crewAccept;
    }

    public void setCrewRole(CrewRole crewRole) {
        this.crewRole = crewRole;
    }

    public void setIndividualColor(String labelColor) {
        this.individualColor = labelColor;
    }
}
