package tosiltosil.backend.module.stopwatch.application;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.common.messaging.Events;
import tosiltosil.backend.module.duration.application.DurationService;
import tosiltosil.backend.module.goal.application.GoalService;
import tosiltosil.backend.module.stopwatch.domain.Stopwatch;
import tosiltosil.backend.module.stopwatch.domain.StopwatchRepository;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchPausedEvent;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchStartedEvent;

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
        // START 가능한지 여부 검증
        goalService.changeStatusToStarted(memberId, goalId);

        // 스탑워치 새로운 기록 생성
        Stopwatch stopwatch = Stopwatch.of(goalId);
        stopwatchRepository.save(stopwatch);

        // 오늘 총 진행 시간 가져오기 - 방금 시작된 스톱워치 시간은 제외
        Duration todayDuration = durationService.getTodayDuration(memberId);

        // 스탑워치 시작 메세지 전송
        Events.raise(
                StopwatchStartedEvent.of(memberId, stopwatch, todayDuration)
        );
    }

    public void pauseStopwatch(
            final UUID memberId,
            final Long goalId
    ) {
        // PAUSE 가능한지 여부 검증
        goalService.changeStatusToPaused(memberId, goalId);

        // 스탑워치 끝 시각 업데이트
        Stopwatch stopwatch = stopwatchRepository.findLatestByGoalId(goalId)
                .orElseThrow(() -> new NotFoundException("스톱워치 데이터가 존재하지 않습니다."));
        stopwatch.updateEndAt();

        // 오늘 총 진행 시간 업데이트
        Duration updatedTodayDuration = durationService.updateTodayDuration(memberId, stopwatch.getDuration());

        // 스탑워치 정지 메세지 전송 + 목표 진행 시간 업데이트
        Events.raise(
                StopwatchPausedEvent.of(memberId, goalId, stopwatch, updatedTodayDuration)
        );
    }
}
