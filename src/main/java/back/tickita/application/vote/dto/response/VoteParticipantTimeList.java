package back.tickita.application.vote.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class VoteParticipantTimeList {
    private Set<ParticipantTime> participantTimes;

    public void setParticipantTimes(Set<ParticipantTime> participantTimes) {
        this.participantTimes = participantTimes;
    }
}
