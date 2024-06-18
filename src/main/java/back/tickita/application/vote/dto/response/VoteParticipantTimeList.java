package back.tickita.application.vote.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class VoteParticipantTimeList {
    private List<ParticipantTime> participantTimes;

    public void setParticipantTimes(List<ParticipantTime> participantTimes) {
        this.participantTimes = participantTimes;
    }
}
