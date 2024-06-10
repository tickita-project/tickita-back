package back.tickita.application.schedule.controller;

import back.tickita.application.account.dto.request.LoginUserInfo;
import back.tickita.application.schedule.dto.request.ScheduleRequest;
import back.tickita.application.schedule.dto.response.FilteredScheduleResponse;
import back.tickita.application.schedule.dto.response.MessageResponse;
import back.tickita.application.schedule.dto.response.ScheduleResponse;
import back.tickita.application.schedule.service.ScheduleService;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import back.tickita.interceptor.annotation.LoginUser;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "일정 API", description = "ScheduleController")
@RestController
@AllArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final AccountRepository accountRepository;

    @PostMapping
    @Operation(summary = "일정 등록", description = "일정을 등록합니다.")
    public ResponseEntity<ScheduleResponse> createSchedule(@LoginUser LoginUserInfo loginUserInfo, @RequestBody ScheduleRequest request) {
        Account account = accountRepository.findById(loginUserInfo.accountId())
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        return ResponseEntity.ok(scheduleService.createSchedule(request));
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "일정 상세보기", description = "등록된 일정의 상세 정보를 조회합니다.")
    public ResponseEntity<ScheduleResponse> getScheduleById(@LoginUser LoginUserInfo loginUserInfo, @PathVariable Long scheduleId) {
        Account account = accountRepository.findById(loginUserInfo.accountId())
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        return ResponseEntity.ok(scheduleService.getScheduleById(scheduleId));
    }

    @PutMapping("/{scheduleId}")
    @Operation(summary = "일정 수정", description = "등록된 일정을 정보를 수정합니다.")
    public ResponseEntity<ScheduleResponse> updateSchedule(@LoginUser LoginUserInfo loginUserInfo,
                                                           @PathVariable Long scheduleId, @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(loginUserInfo.accountId(), scheduleId, request));
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "일정 삭제", description = "등록된 일정을 삭제합니다.")
    public ResponseEntity<MessageResponse> deleteSchedule(@LoginUser LoginUserInfo loginUserInfo, @PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.deleteSchedule(loginUserInfo.accountId(), scheduleId));
    }

    @GetMapping("/filter")
    @Operation(summary = "일정 조회", description = "선택한 그룹, 기간의 일정 중 사용자가 참석자로 포함된 일정을 조회합니다.")
    public ResponseEntity<List<FilteredScheduleResponse>> getFilteredSchedules(@LoginUser LoginUserInfo loginUserInfo, @RequestParam List<Long> crewIds,
                                                                               @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(scheduleService.getFilteredSchedules(loginUserInfo.accountId(), crewIds, startDate, endDate));
    }
}