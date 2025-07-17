package tosiltosil.backend.module.goal.application;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.common.domain.order.OrderManager;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.request.GoalOrderChangeRequest;
import tosiltosil.backend.module.goal.domain.request.GoalUpdateRequest;
import tosiltosil.backend.module.goal.domain.response.DayGoalListResponse;
import tosiltosil.backend.module.goal.domain.response.GoalIdResponse;
import tosiltosil.backend.module.goal.domain.response.GoalIdsResponse;
import tosiltosil.backend.module.goal.domain.response.GoalOrderChangeResponse;
import tosiltosil.backend.module.goal.domain.service.GoalDomainService;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalDomainService goalDomainService;
    private final OrderManager orderManager;

    @Transactional(readOnly = true)
    public List<DayGoalListResponse> getDayGoals(
            final UUID memberOwnerId,
            final UUID memberId,
            final LocalDate date
    ) {
        // TODO: 친구 관계 검증

        return goalRepository.findDayGoals(memberId, date);
    }

    @Transactional
    public GoalIdsResponse createGoal(
            final UUID memberId,
            final GoalCreateRequest request
    ) {
        request.dates().forEach(dateString -> {
            LocalDate date = LocalDate.parse(dateString);
            goalDomainService.validateGoalDate(date);
        });

        // 마지막 저장된 Goal 순서 가져오기
        BigDecimal lastOrderIndex = goalRepository.findLastOrderIndex(memberId)
                .orElse(orderManager.generateInitialOrderIndex());

        List<Goal> goals = new ArrayList<>();
        for (String date : request.dates()) {
            // 마지막 순서 불러오기
            BigDecimal newOrderIndex = orderManager.generateOrderIndexBetween(lastOrderIndex, null);
            Goal goal = Goal.of(
                    memberId,
                    request.categoryId(),
                    request.title(),
                    Duration.parse(request.time()),
                    // 마지막 순서 다음으로 저장
                    newOrderIndex,
                    request.iconId(),
                    LocalDate.parse(date)
            );
            goals.add(goal);
            // lastOrderIndex 업데이트
            lastOrderIndex = newOrderIndex;
        }

        List<Long> savedGoalIds = goalRepository.saveAll(goals).stream()
                .map(Goal::getId)
                .toList();
        return GoalIdsResponse.of(savedGoalIds);
    }

    @Transactional
    public GoalIdResponse updateGoal(
            final UUID memberId,
            final Long goalId,
            final GoalUpdateRequest request
    ) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new NotFoundException("목표가 존재하지 않습니다."));
        goal.validateIsMine(memberId);

        LocalDate newDate = LocalDate.parse(request.date());
        goalDomainService.validateGoalDate(newDate);

        goal.updateBasicInfo(request.title(), request.categoryId(), request.iconId());
        goal.changeDate(newDate);

        return GoalIdResponse.of(goal.getId());
    }

    @Transactional
    public GoalOrderChangeResponse changeOrder(
            final UUID memberId,
            final Long goalId,
            final GoalOrderChangeRequest request
    ) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new NotFoundException("목표가 존재하지 않습니다."));
        goal.validateIsMine(memberId);

        if (orderManager.validateIndexBounds(request.prevOrderIndex(), request.nextOrderIndex())) {
            renewOrderIndexes(memberId, goal.getCategoryId());
        }

        BigDecimal newOrderIndex = orderManager.generateOrderIndexBetween(request.prevOrderIndex(), request.nextOrderIndex());
        goal.updateOrderIndex(newOrderIndex);
        
        goalRepository.save(goal);

        return GoalOrderChangeResponse.of(newOrderIndex);
    }

    private void renewOrderIndexes(final UUID memberId, final Long categoryId) {
        List<Goal> goals = goalRepository.findTodayGoalsInCategory(memberId, categoryId);

        List<Goal> renewedGoals = orderManager.renewOrderIndexes(goals);

        goalRepository.saveAll(renewedGoals);
    }

    @Transactional
    public GoalIdResponse deleteGoal(
            final UUID memberId,
            final Long goalId
    ) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new NotFoundException("목표가 존재하지 않습니다."));
        goal.validateIsMine(memberId);

        goalRepository.delete(goal);
        return GoalIdResponse.of(goal.getId());
    }

    @Transactional
    public Duration deleteGoalsAndCalculateTotalDuration(final UUID memberId, final Long categoryId) {
        List<Goal> goalsToDelete = goalRepository.findTotalGoals(memberId, categoryId);

        Duration totalDuration = goalsToDelete.stream()
                .map(Goal::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        goalsToDelete.forEach(goalRepository::delete);

        return totalDuration;
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
}