package back.tickita.domain.vote.entity;


import back.tickita.common.BaseEntity;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.vote.enums.VoteEndType;
import back.tickita.domain.vote.enums.VoteType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteList extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private CrewList crewList;

    @ManyToOne(fetch = FetchType.LAZY)
    private VoteSubject voteSubject;

    private Boolean voteParticipateType = false;

    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    @OneToMany(mappedBy = "voteList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteComplete> complete;

    public VoteList(CrewList crewList, VoteSubject voteSubject, Boolean voteParticipateType, VoteType voteType) {
        this.crewList = crewList;
        this.voteSubject = voteSubject;
        this.voteParticipateType = voteParticipateType;
        this.voteType = voteType;
    }

    public static VoteList creator(CrewList crewList, VoteSubject voteSubject){
        return new VoteList(crewList, voteSubject, true, VoteType.CREATOR);
    }

    public static VoteList participant(CrewList crewList, VoteSubject voteSubject) {
        return new VoteList(crewList, voteSubject, false, VoteType.PARTICIPANT);
    }

    public void setVoteType(Boolean voteParticipateType) {
        this.voteParticipateType = voteParticipateType;
    }

    public Account getAccount() {
        return crewList != null ? crewList.getAccount() : null;
    }
    public VoteEndType getVoteEndType() {
        return voteSubject != null ? voteSubject.getVoteEndType() : null;
    }
    public String getParticipateName() {
        return crewList != null && crewList.getAccountName() != null ? crewList.getAccountName() : null;
    }
}
