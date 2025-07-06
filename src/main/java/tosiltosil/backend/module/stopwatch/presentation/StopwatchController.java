package tosiltosil.backend.module.stopwatch.presentation;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tosiltosil.backend.common.auth.annotation.LoginMember;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.stopwatch.application.StopwatchService;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class StopwatchController {

    private final StopwatchService stopwatchService;

    @PostMapping("/{goalId}/stopwatch/start")
    public Response<Void> startStopwatch(
            @LoginMember final UUID memberId,
            @PathVariable final Long goalId
    ) {
        stopwatchService.startStopwatch(memberId, goalId);
        return Response.ok("스톱워치가 정상적으로 시작되었습니다.", null);
    }

    @PostMapping("/{goalId}/stopwatch/pause")
    public Response<Void> pauseStopwatch(
            @LoginMember final UUID memberId,
            @PathVariable final Long goalId
    ) {
        stopwatchService.pauseStopwatch(memberId, goalId);
        return Response.ok("스톱워치가 정상적으로 정지되었습니다.", null);
    }
}