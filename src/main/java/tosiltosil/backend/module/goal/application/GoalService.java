package tosiltosil.backend.module.goal.application;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.goal.domain.DailyTotalTime;
import tosiltosil.backend.module.goal.domain.DailyTotalTimeRepository;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.request.GoalSequenceChangeRequest;
import tosiltosil.backend.module.goal.domain.request.GoalUpdateRequest;
import tosiltosil.backend.module.goal.domain.response.GoalResponse;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final DailyTotalTimeRepository dailyTotalTimeRepository;

    @Transactional(readOnly = true)
    public void varifyCreateGoal(
            final UUID memberId
    ) {
        DailyTotalTime dailyTotalTime = dailyTotalTimeRepository.findByMemberId(memberId);

        dailyTotalTime.validateDurationUnder24Hours();
    }

    @Transactional
    public GoalResponse createGoal(
            final UUID memberId,
            final GoalCreateRequest request
    ) {
        varifyCreateGoal(memberId);
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
        Goal goal = goalRepository.findByIdAndMemberId(goalId, memberId)
                .orElseThrow(() -> new NotFoundException("목표를 찾을 수 없습니다."));

        goal.updateBasicInfo(request.title(), request.categoryId(), request.iconId());
        goal.changeDate(request.date());

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
        Goal goal = goalRepository.findByIdAndMemberId(goalId, memberId)
                .orElseThrow(() -> new NotFoundException("목표를 찾을 수 없습니다."));
        goalRepository.delete(goal);
        return GoalResponse.ofSingle(goal.getId());
    }
}