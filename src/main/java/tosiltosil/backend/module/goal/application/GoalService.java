package tosiltosil.backend.module.goal.application;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.common.domain.order.OrderManager;
import tosiltosil.backend.common.messaging.Events;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.module.goal.domain.event.GoalDeletedEvent;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.request.GoalOrderChangeRequest;
import tosiltosil.backend.module.goal.domain.request.GoalUpdateRequest;
import tosiltosil.backend.module.goal.domain.response.DayGoalsResponse;
import tosiltosil.backend.module.goal.domain.response.GoalListPerCategoryResponse;
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
    public DayGoalsResponse getDayGoals(
            final UUID memberOwnerId,
            final UUID memberId,
            final LocalDate date
    ) {
        // TODO: 친구 관계 검증
        
        List<GoalListPerCategoryResponse> categoryResponses = goalRepository.findDayGoals(memberId, date);

        BigDecimal overallPercentage = goalDomainService.calculateGoalAchievedPercentage(memberId);

        return DayGoalsResponse.of(overallPercentage, categoryResponses);
    }

    @Transactional(readOnly = true)
    public Duration getGoalTotalDuration(
            final UUID memberId,
            final Long categoryId
    ) {
        List<Goal> goals = goalRepository.findTotalGoals(memberId, categoryId);

        return goals.stream()
                .map(Goal::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Transactional
    public GoalIdsResponse createGoal(
            final UUID memberId,
            final GoalCreateRequest request
    ) {
        // 날짜 검증
        request.dates().forEach(dateString -> {
            LocalDate date = LocalDate.parse(dateString);
            goalDomainService.validateGoalDate(date);
        });

        // 날짜에 맞는 순서 인덱스 생성
        BigDecimal lastOrderIndex = goalRepository.findLastOrderIndex(memberId).orElse(null);
        List<BigDecimal> newOrderIndexes = orderManager.generateSequentialOrderIndexes(lastOrderIndex, request.dates().size());

        // 목표 생성 시 순서 인덱스 부여
        List<Goal> goals = IntStream.range(0, request.dates().size())
                .mapToObj(i -> Goal.of(
                        memberId,
                        request.categoryId(),
                        request.title(),
                        Duration.parse(request.time()),
                        newOrderIndexes.get(i),
                        request.iconId(),
                        LocalDate.parse(request.dates().get(i))
                ))
                .toList();

        // 저장
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
        // 목표 본인 것인지 검증
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new NotFoundException("목표가 존재하지 않습니다."));
        goal.validateIsMine(memberId);

        // 해당 카테고리의 오늘 목표들을 순서대로 가져오기
        List<Goal> goals = goalRepository.findTodayGoalsInCategory(memberId, goal.getCategoryId());
        
        // OrderManager를 사용하여 새로운 순서 인덱스 계산
        BigDecimal newOrderIndex = orderManager.calculateOrderIndexForPosition(goals, request.targetPosition());
        goal.updateOrderIndex(newOrderIndex);

        // 저장
        goalRepository.save(goal);

        return GoalOrderChangeResponse.of(newOrderIndex);
    }

    @Transactional
    public GoalIdResponse deleteGoal(
            final UUID memberId,
            final Long goalId
    ) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new NotFoundException("목표가 존재하지 않습니다."));
        goal.validateIsMine(memberId);

        // 목표 삭제
        goalRepository.delete(goal);

        // 사용자 목표 성취 시간 차감
        Events.raise(
                GoalDeletedEvent.of(memberId, goal.getDuration())
        );

        return GoalIdResponse.of(goal.getId());
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