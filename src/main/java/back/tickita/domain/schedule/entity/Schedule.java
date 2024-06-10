package back.tickita.domain.schedule.entity;

import back.tickita.application.schedule.dto.request.ScheduleRequest;
import back.tickita.application.schedule.dto.response.ScheduleResponse;
import back.tickita.common.BaseEntity;
import back.tickita.domain.crews.entity.Crews;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Schedule extends BaseEntity {

    private String title;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private String location;

    private String description;

    @ManyToOne
    @JoinColumn(name = "crews_id")
    private Crews crews;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<Participant> participants = new ArrayList<>();

    public void setSchedule(ScheduleRequest request, Crews crews, List<Participant> participants) {
        this.title = request.getTitle();
        this.startDateTime = request.getStartDateTime();
        this.endDateTime = request.getEndDateTime();
        this.location = request.getLocation();
        this.description = request.getDescription();
        this.crews = crews;
        this.participants.clear();

        // 각 Participant에 Schedule 설정
        for (Participant participant : participants) {
            participant.setSchedule(this);
            this.participants.add(participant);
        }
    }

    public ScheduleResponse toScheduleResponse() {
        List<Long> participantIds = participants.stream()
                .map(Participant::getId)
                .collect(Collectors.toList());

        return new ScheduleResponse(id, title, startDateTime, endDateTime, location, description, crews.getId(), participantIds);
    }
}
