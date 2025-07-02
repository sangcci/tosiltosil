package tosiltosil.backend.module.stopwatch.presentation;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.stopwatch.application.StopwatchService;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class StopwatchController implements StopwatchApiSpecification {

    private final StopwatchService stopwatchService;

    @PostMapping("/{goalId}/stopwatch/start")
    @Override
    public Response<Void> startStopwatch(
            @PathVariable @NotNull(message = "목표 ID가 유효하지 않습니다. 목표 ID 숫자를 입력해야 합니다.") final Long goalId,
            final UUID memberId
    ) {
        stopwatchService.startStopwatch(memberId, goalId);
        return Response.ok("스톱워치가 정상적으로 시작되었습니다.", null);
    }

    @PostMapping("/{goalId}/stopwatch/pause")
    @Override
    public Response<Void> pauseStopwatch(
            @PathVariable @NotNull(message = "목표 ID가 유효하지 않습니다. 목표 ID 숫자를 입력해야 합니다.") final Long goalId,
            final UUID memberId
    ) {
        stopwatchService.pauseStopwatch(memberId, goalId);
        return Response.ok("스톱워치가 정상적으로 정지되었습니다.", null);
    }
}