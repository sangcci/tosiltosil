package tosiltosil.backend.module.stopwatch.application;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.common.messaging.Events;
import tosiltosil.backend.module.duration.application.DurationService;
import tosiltosil.backend.module.goal.application.GoalService;
import tosiltosil.backend.module.stopwatch.domain.Stopwatch;
import tosiltosil.backend.module.stopwatch.domain.StopwatchRepository;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchStatusChangedEvent;

@Service
@RequiredArgsConstructor
@Transactional
public class StopwatchService {

    private final StopwatchRepository stopwatchRepository;
    private final GoalService goalService;
    private final DurationService durationService;

    public void startStopwatch(
            final UUID memberId,
            final Long goalId
    ) {
        goalService.changeStatusToStarted(memberId, goalId);

        Stopwatch stopwatch = Stopwatch.of(goalId);
        stopwatchRepository.save(stopwatch);

        Duration todayDuration = durationService.getTodayDuration(memberId);

        Events.raise(StopwatchStatusChangedEvent.of(memberId, "STARTED", stopwatch.getStartedAt(), todayDuration));
    }

    public void pauseStopwatch(
            final UUID memberId,
            final Long goalId
    ) {
        goalService.changeStatusToPaused(memberId, goalId);

        Stopwatch stopwatch = stopwatchRepository.findLatestByGoalId(goalId)
                .orElseThrow(() -> new IllegalArgumentException("스톱워치 데이터가 존재하지 않습니다."));
        stopwatch.updateEndAt();

        Duration updatedTodayDuration = durationService.updateTodayDuration(memberId, stopwatch.getDuration());

        Events.raise(StopwatchStatusChangedEvent.of(memberId, "PAUSED", stopwatch.getStartedAt(), updatedTodayDuration));
    }

    public void completeStopwatch(
            final UUID memberId,
            final Long goalId
    ) {
        goalService.changeStatusToCompleted(memberId, goalId);

        Stopwatch stopwatch = stopwatchRepository.findLatestByGoalId(goalId)
                .orElseThrow(() -> new IllegalArgumentException("스톱워치 데이터가 존재하지 않습니다."));
        stopwatch.updateEndAt();

        Duration updatedTodayDuration = durationService.updateTodayDuration(memberId, stopwatch.getDuration());

        Events.raise(StopwatchStatusChangedEvent.of(memberId, "COMPLETED", stopwatch.getStartedAt(), updatedTodayDuration));
    }
}
