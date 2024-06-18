package back.tickita.domain.vote.entity;

import back.tickita.application.vote.dto.request.VoteParticipantRequest;
import back.tickita.common.BaseEntity;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.vote.enums.VoteEndType;
import back.tickita.domain.vote.enums.VoteType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteSubject extends BaseEntity {

    private String title;

    private String content;

    private String place;

    private LocalTime endTime;

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Crews crews;

    @Enumerated(EnumType.STRING)
    private VoteEndType voteEndType;

    @OneToMany(mappedBy = "voteSubject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteState> voteState;

    public VoteSubject(String title, String content, String place, LocalTime endTime, LocalDate endDate, Crews crews, VoteEndType voteEndType) {
        this.title = title;
        this.content = content;
        this.place = place;
        this.endTime = endTime;
        this.endDate = endDate;
        this.crews = crews;
        this.voteEndType = voteEndType;
    }

    public static VoteSubject create(String title, String content, String place, LocalTime endTime, LocalDate endDate, Crews crews){
        return new VoteSubject(title, content, place, endTime, endDate, crews, VoteEndType.PROGRESS);
    }

    public void update(){
        this.voteEndType = VoteEndType.FINISH;
    }

    public String getRemainTime() {
        LocalDateTime endDateTime = LocalDateTime.of(this.endDate, this.endTime);
        Duration duration = Duration.between(LocalDateTime.now(), endDateTime);

        if (duration.isNegative()){
            return "마감됨";
        }
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        if (days > 0) {
            return days + "일 남음";
        }else if (hours > 0) {
            return hours + "시간 남음";
        }else {
            return minutes + "분 남음";
        }
    }
}
