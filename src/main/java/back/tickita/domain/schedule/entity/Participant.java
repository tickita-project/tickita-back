package back.tickita.domain.schedule.entity;

import back.tickita.domain.account.entity.Account;
import back.tickita.domain.crews.entity.Crews;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public Participant(Account account) {
        this.account = account;
    }
}
