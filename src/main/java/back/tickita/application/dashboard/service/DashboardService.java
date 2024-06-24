package back.tickita.application.dashboard.service;

import back.tickita.application.crews.dto.response.CrewAllInfo;
import back.tickita.application.dashboard.dto.EventInfo;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.schedule.entity.Schedule;
import back.tickita.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ScheduleRepository scheduleRepository;

    public List<EventInfo> getUpcomingEvents(Long accountId) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDate currentDate = LocalDate.now();
        LocalDateTime startOfCurrentDay = currentDate.atStartOfDay();

        List<Schedule> schedules = scheduleRepository.findTop10ByAccountIdAndStartDateTimeAfterOrOnSameDayOrderByStartDateTime(accountId, currentDateTime, startOfCurrentDay);

        return schedules.stream()
                .map(schedule -> {
                    LocalDateTime eventStartDateTime = schedule.getStartDateTime();
                    LocalDate eventDate = eventStartDateTime.toLocalDate();
                    String remainTime;

                    if (eventDate.equals(currentDate)) {
                        remainTime = "D-DAY";
                    } else {
                        long daysBetween = ChronoUnit.DAYS.between(currentDate, eventDate);
                        remainTime = "D-" + daysBetween;
                    }

                    Crews crews = schedule.getCrews();
                    String individualColor = crews.getCrewLists().stream()
                            .filter(crewList -> crewList.getAccount().getId().equals(accountId))
                            .map(CrewList::getIndividualColor)
                            .filter(color -> color != null && !color.isEmpty())
                            .findFirst()
                            .orElse(crews.getLabelColor());

                    CrewAllInfo crewInfo = new CrewAllInfo(crews.getId(), crews.getCrewName(), individualColor);
                    return new EventInfo(schedule.getId(), schedule.getTitle(), schedule.getStartDateTime(), remainTime, crewInfo);
                })
                .collect(Collectors.toList());
    }
}
