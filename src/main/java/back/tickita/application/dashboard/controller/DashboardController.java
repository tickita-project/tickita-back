package back.tickita.application.dashboard.controller;

import back.tickita.application.account.dto.request.LoginUserInfo;
import back.tickita.application.dashboard.dto.EventInfo;
import back.tickita.application.dashboard.service.DashboardService;
import back.tickita.interceptor.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
@Tag(name = "대시보드 API", description = "DashboardController")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/upcoming-events")
    @Operation(summary = "다가오는 일정 조회", description = "사용자가 포함된 다가오는 일정 중 최근 10개의 일정을 조회합니다.")
    public ResponseEntity<List<EventInfo>> getUpcomingEvents(@LoginUser LoginUserInfo loginUserInfo) {
        return ResponseEntity.ok(dashboardService.getUpcomingEvents(loginUserInfo.accountId()));
    }
}
