package back.tickita.application.schedule.service;

import back.tickita.application.crews.service.CrewsReadService;
import back.tickita.application.schedule.dto.request.ScheduleRequest;
import back.tickita.application.schedule.dto.response.MessageResponse;
import back.tickita.application.schedule.dto.response.ScheduleResponse;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.schedule.entity.Participant;
import back.tickita.domain.schedule.entity.Schedule;
import back.tickita.domain.schedule.repository.ParticipantRepository;
import back.tickita.domain.schedule.repository.ScheduleRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CrewsReadService crewsReadService;
    private final ParticipantRepository participantRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest request) {
        Crews crews = crewsReadService.findById(request.getCrewId())
                .orElseThrow(() -> new IllegalArgumentException("Crew not found"));

        // 요청된 participants와 일치하는 Participant 객체를 생성
        List<Participant> participants = convertToParticipants(request.getParticipants(), crews);

        Schedule schedule = new Schedule();
        schedule.setSchedule(request, crews, participants);
        scheduleRepository.save(schedule);

        return convertToScheduleResponse(schedule);
    }

    private List<Participant> convertToParticipants(List<ScheduleRequest.ParticipantInfo> participantInfos, Crews crews) {
        Set<Long> crewMemberIds = crews.getCrewLists().stream()
                .map(crewMember -> crewMember.getAccount().getId())
                .collect(Collectors.toSet());

        List<Participant> participants = new ArrayList<>();
        for (ScheduleRequest.ParticipantInfo participantInfo : participantInfos) {
            if (!crewMemberIds.contains(participantInfo.getAccountId())) {
                throw new IllegalArgumentException("Participant with ID " + participantInfo.getAccountId() + " does not belong to the specified crew.");
            }
            Account foundAccount  = crews.getCrewLists().stream()
                    .map(CrewList::getAccount)
                    .filter(account -> account.getId().equals(participantInfo.getAccountId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Account with ID " + participantInfo.getAccountId() + " not found in crew."));
            participants.add(new Participant(foundAccount));
        }
        return participants;
    }

    @Transactional(readOnly = true)
    public ScheduleResponse getScheduleById(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));

        return convertToScheduleResponse(schedule);
    }

    @Transactional
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

        // 요청된 participants와 일치하는 Participant 객체를 생성
        List<Participant> participants = convertToParticipants(request.getParticipants(), crews);

        schedule.setSchedule(request, crews, participants);
        scheduleRepository.save(schedule);

        return convertToScheduleResponse(schedule);
    }

    @Transactional
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

        return new MessageResponse(scheduleId, "일정이 성공적으로 삭제되었습니다.");
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getFilteredSchedules(Long accountId, Long crewId, LocalDateTime startDate, LocalDateTime endDate) {
        // 그룹 ID와 사용자 ID로 필터링
        List<Schedule> schedules = participantRepository.findSchedulesByCrewIdAndParticipantId(crewId, accountId);
        // 기간 필터링
        List<Schedule> filteredSchedules = filterSchedulesByDate(schedules, startDate, endDate);

        return filteredSchedules.stream()
                .map(this::convertToScheduleResponse)
                .collect(Collectors.toList());
    }

    public List<Schedule> filterSchedulesByDate(List<Schedule> schedules, LocalDateTime startDate, LocalDateTime endDate) {
        return schedules.stream()
                .filter(schedule -> {
                    LocalDateTime scheduleStartDate = schedule.getStartDateTime();
                    LocalDateTime scheduleEndDate = schedule.getEndDateTime();
                    return !scheduleEndDate.isBefore(startDate) && !scheduleStartDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    private ScheduleResponse convertToScheduleResponse(Schedule schedule) {
        List<ScheduleResponse.ParticipantInfo> participantInfos = schedule.getParticipants().stream()
                .map(participant -> new ScheduleResponse.ParticipantInfo(
                        participant.getAccount().getId(),
                        participant.getAccount().getNickName()))
                .collect(Collectors.toList());

        return new ScheduleResponse(schedule.getId(), schedule.getTitle(), schedule.getStartDateTime(), schedule.getEndDateTime(),
                schedule.getLocation(), schedule.getDescription(), schedule.getCrews().getId(), schedule.getCrews().getCrewName(), schedule.getCrews().getLabelColor(), participantInfos);
    }
}