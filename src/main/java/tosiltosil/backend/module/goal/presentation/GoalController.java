package tosiltosil.backend.module.goal.presentation;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tosiltosil.backend.common.auth.annotation.LoginMember;
import tosiltosil.backend.common.domain.validator.IsDate;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.goal.application.GoalService;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.request.GoalOrderChangeRequest;
import tosiltosil.backend.module.goal.domain.request.GoalRenewOrderRequest;
import tosiltosil.backend.module.goal.domain.request.GoalUpdateRequest;
import tosiltosil.backend.module.goal.domain.response.DayGoalListResponse;
import tosiltosil.backend.module.goal.domain.response.GoalIdResponse;
import tosiltosil.backend.module.goal.domain.response.GoalIdsResponse;
import tosiltosil.backend.module.goal.domain.response.GoalOrderChangeResponse;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping("/members/{memberId}")
    public Response<List<DayGoalListResponse>> getGoalsByMemberId(
            @LoginMember final UUID memberOwnerId,
            @PathVariable final UUID memberId,
            @RequestParam @IsDate final LocalDate date
    ) {
        List<DayGoalListResponse> responses = goalService.getDayGoals(memberOwnerId, memberId, date);
        return Response.ok("목표 리스트 조회 성공", responses);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<GoalIdsResponse> createGoal(
            @LoginMember final UUID memberId,
            @RequestBody @Valid final GoalCreateRequest request
    ) {
        GoalIdsResponse response = goalService.createGoal(memberId, request);
        return Response.create("목표가 정상적으로 생성되었습니다.", response);
    }

    @PatchMapping("/{goalId}")
    public Response<GoalIdResponse> updateGoal(
            @LoginMember final UUID memberId,
            @PathVariable final Long goalId,
            @RequestBody @Valid final GoalUpdateRequest request
    ) {
        GoalIdResponse response = goalService.updateGoal(memberId, goalId, request);
        return Response.ok("목표가 정상적으로 수정되었습니다.", response);
    }

    @PatchMapping("/{goalId}/change-order")
    public Response<GoalOrderChangeResponse> changeGoalOrder(
            @LoginMember final UUID memberId,
            @PathVariable final Long goalId,
            @RequestBody @Valid final GoalOrderChangeRequest request
    ) {
        GoalOrderChangeResponse response = goalService.changeOrder(memberId, goalId, request);
        return Response.ok("목표 순서가 정상적으로 변경되었습니다.", response);
    }

    @PostMapping("/renew-order")
    public Response<Map<String, Object>> renewOrderIndexes(
            @LoginMember final UUID memberId,
            @RequestBody final GoalRenewOrderRequest request
    ) {
        goalService.renewOrderIndexes(memberId, request);
        return Response.ok("목표 순서가 정상적으로 갱신되었습니다.");
    }

    @DeleteMapping("/{goalId}")
    public Response<GoalIdResponse> deleteGoal(
            @LoginMember final UUID memberId,
            @PathVariable final Long goalId
    ) {
        GoalIdResponse response = goalService.deleteGoal(memberId, goalId);
        return Response.ok("목표가 정상적으로 삭제되었습니다.", response);
    }
}
