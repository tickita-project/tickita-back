package back.tickita.application.schedule.service;

import back.tickita.application.crews.service.CrewsReadService;
import back.tickita.application.schedule.dto.request.ScheduleRequest;
import back.tickita.application.schedule.dto.response.FilteredScheduleResponse;
import back.tickita.application.schedule.dto.response.MessageResponse;
import back.tickita.application.schedule.dto.response.ScheduleResponse;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.schedule.entity.Participant;
import back.tickita.domain.schedule.entity.Schedule;
import back.tickita.domain.schedule.repository.ParticipantRepository;
import back.tickita.domain.schedule.repository.ScheduleRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ScheduleService {

    private ScheduleRepository scheduleRepository;
    private CrewsReadService crewsReadService;
    private ParticipantRepository participantRepository;
    private AccountRepository accountRepository;

    public ScheduleResponse createSchedule(ScheduleRequest request) {
        Crews crews = crewsReadService.findById(request.getCrewId())
                .orElseThrow(() -> new IllegalArgumentException("Crew not found"));

        // 요청된 participantId와 일치하는 accountId를 가진 Participant 객체를 생성
        List<Participant> participants = convertToParticipants(request.getParticipantIds(), crews);

        Schedule schedule = new Schedule();
        schedule.setSchedule(request, crews, participants);
        scheduleRepository.save(schedule);

        List<Long> participantIds = getParticipantIds(schedule);

        return new ScheduleResponse(schedule.getId(), schedule.getTitle(), schedule.getStartTime(), schedule.getEndTime(),
                schedule.getLocation(), schedule.getDescription(), schedule.getCrews().getId(), participantIds);
    }

    private List<Participant> convertToParticipants(List<Long> participantIds, Crews crews) {
        return crews.getCrewLists().stream()
                .filter(participantInfo -> participantIds.contains(participantInfo.getAccount().getId()))
                .map(participantInfo -> new Participant(participantInfo.getAccount()))
                .collect(Collectors.toList());
    }

    private List<Long> getParticipantIds(Schedule schedule) {
        return schedule.getParticipants().stream()
                .map(participant -> participant.getAccount().getId())
                .collect(Collectors.toList());
    }

    public ScheduleResponse getScheduleById(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));

        List<Long> participantIds = getParticipantIds(schedule);

        return new ScheduleResponse(schedule.getId(), schedule.getTitle(), schedule.getStartTime(), schedule.getEndTime(),
                schedule.getLocation(), schedule.getDescription(), schedule.getCrews().getId(), participantIds);
    }

    public ScheduleResponse updateSchedule(Long accountId, Long scheduleId, ScheduleRequest request) {

        Account updater = accountRepository.findById(accountId)
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));

        if (schedule.getParticipants().stream()
                .noneMatch(participant -> participant.getAccount().getId().equals(updater.getId()))) {
            throw new SecurityException("해당 일정을 변경할 권한이 없습니다.");
        }

        Crews crews = crewsReadService.findById(request.getCrewId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

        // 요청된 participantId와 일치하는 accountId를 가진 Participant 객체를 생성
        List<Participant> participants = crews.getCrewLists().stream()
                .filter(participantInfo -> request.getParticipantIds().contains(participantInfo.getAccount().getId()))
                .map(participantInfo -> {
                    return new Participant(participantInfo.getAccount(), schedule);
                })
                .collect(Collectors.toList());

        schedule.setSchedule(request, crews, participants);
        scheduleRepository.save(schedule);

        List<Long> participantIds = getParticipantIds(schedule);

        return new ScheduleResponse(schedule.getId(), schedule.getTitle(), schedule.getStartTime(), schedule.getEndTime(),
                schedule.getLocation(), schedule.getDescription(), schedule.getCrews().getId(), participantIds);
    }

    public MessageResponse deleteSchedule(Long accountId, Long scheduleId) {

        Account deleter = accountRepository.findById(accountId)
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));

        if (schedule.getParticipants().stream()
                .noneMatch(participant -> participant.getAccount().getId().equals(deleter.getId()))) {
            throw new SecurityException("해당 일정을 삭제할 권한이 없습니다.");
        }
        scheduleRepository.delete(schedule);

        return new MessageResponse(scheduleId,"일정이 성공적으로 삭제되었습니다.");
    }

    public List<FilteredScheduleResponse> getFilteredSchedules(Long accountId, List<Long> crewIds, LocalDateTime startDate, LocalDateTime endDate) {
        // 그룹 ID와 사용자 ID로 필터링
        List<Schedule> schedules = participantRepository.findSchedulesByCrewIdsAndParticipantId(crewIds, accountId);

        // 기간 필터링
        List<Schedule> filteredSchedules = filterSchedulesByDate(schedules, startDate, endDate);

        // 일정을 그룹 ID로 그룹화
        Map<Long, List<ScheduleResponse>> groupedSchedules = groupSchedulesByCrewId(filteredSchedules);

        return groupedSchedules.entrySet().stream()
                .map(entry -> new FilteredScheduleResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<Schedule> filterSchedulesByDate(List<Schedule> schedules, LocalDateTime startDate, LocalDateTime endDate) {
        return schedules.stream()
                .filter(schedule -> {
                    LocalDateTime scheduleStartDate = schedule.getStartTime();
                    LocalDateTime scheduleEndDate = schedule.getEndTime();
                    return !scheduleEndDate.isBefore(startDate) && !scheduleStartDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    private Map<Long, List<ScheduleResponse>> groupSchedulesByCrewId(List<Schedule> schedules) {
        Map<Long, List<ScheduleResponse>> groupedSchedules = new HashMap<>();

        // 일정을 그룹 ID로 그룹화
        for (Schedule schedule : schedules) {
            Long crewId = schedule.getCrews().getId();
            List<ScheduleResponse> groupSchedules = groupedSchedules.getOrDefault(crewId, new ArrayList<>());
            groupSchedules.add(convertToScheduleResponse(schedule));
            groupedSchedules.put(crewId, groupSchedules);
        }
        return groupedSchedules;
    }

    private ScheduleResponse convertToScheduleResponse(Schedule schedule) {
        List<Long> participantIds = schedule.getParticipants().stream()
                .map(participant -> participant.getAccount().getId())
                .collect(Collectors.toList());
        return new ScheduleResponse(schedule.getId(), schedule.getTitle(), schedule.getStartTime(), schedule.getEndTime(),
                schedule.getLocation(), schedule.getDescription(), schedule.getCrews().getId(), participantIds);
    }
}