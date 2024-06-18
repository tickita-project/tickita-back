package back.tickita.domain.vote.entity;

import back.tickita.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteComplete extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private VoteList voteList;

    @ManyToOne(fetch = FetchType.LAZY)
    private VoteState voteState;

    public static VoteComplete complete(VoteList voteList, VoteState voteState){
        return new VoteComplete(voteList, voteState);
    }
}
