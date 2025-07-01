package tosiltosil.backend.module.goal.application;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.request.GoalSequenceChangeRequest;
import tosiltosil.backend.module.goal.domain.request.GoalUpdateRequest;
import tosiltosil.backend.module.goal.domain.response.GoalResponse;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchPausedEvent;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    @Transactional
    public GoalResponse createGoal(
            final UUID memberId,
            final GoalCreateRequest request
    ) {
        // TODO: 순서 구현

        List<Goal> goals = request.toEntities(memberId);
        List<Long> savedGoalIds = goalRepository.saveAll(goals).stream()
                .map(Goal::getId)
                .toList();
        return GoalResponse.ofList(savedGoalIds);
    }

    @Transactional
    public GoalResponse updateGoal(
            final UUID memberId,
            final Long goalId,
            final GoalUpdateRequest request
    ) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new NotFoundException("목표가 존재하지 않습니다."));
        goal.validateIsMine(memberId);

        goal.updateBasicInfo(request.title(), request.categoryId(), request.iconId());
        goal.changeDate(LocalDate.parse(request.date()));

        return GoalResponse.ofSingle(goal.getId());
    }

    @Transactional
    public void changeSequence(
            final UUID memberId,
            final Long goalId,
            final GoalSequenceChangeRequest request
    ) {
        // TODO: 순서 구현
    }

    @Transactional
    public GoalResponse deleteGoal(
            final UUID memberId,
            final Long goalId
    ) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new NotFoundException("목표가 존재하지 않습니다."));
        goal.validateIsMine(memberId);

        goalRepository.delete(goal);
        return GoalResponse.ofSingle(goal.getId());
    }

    @Transactional
    public void changeStatusToStarted(
            final UUID memberId,
            final Long goalId
    ) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new NotFoundException("목표가 존재하지 않습니다."));
        goal.validateIsMine(memberId);

        goal.changeStatusToStarted();
    }

    @Transactional
    public void changeStatusToPaused(
            final UUID memberId,
            final Long goalId
    ) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new NotFoundException("목표가 존재하지 않습니다."));
        goal.validateIsMine(memberId);

        goal.changeStatusToPaused();
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void addDuration(final StopwatchPausedEvent event) {
        Goal goal = goalRepository.findById(event.goalId())
                .orElseThrow(() -> new NotFoundException("목표가 존재하지 않습니다."));

        Duration addedDuration = Duration.between(event.startTime(), event.endTime());
        goal.addDuration(addedDuration);
    }
}