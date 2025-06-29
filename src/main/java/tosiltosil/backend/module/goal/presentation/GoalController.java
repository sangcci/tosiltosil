package tosiltosil.backend.module.goal.presentation;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.goal.application.GoalService;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.request.GoalSequenceChangeRequest;
import tosiltosil.backend.module.goal.domain.request.GoalUpdateRequest;
import tosiltosil.backend.module.goal.domain.response.GoalCreateValidateResponse;
import tosiltosil.backend.module.goal.domain.response.GoalResponse;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController implements GoalApiSpecification {

    private final GoalService goalService;

    @GetMapping("/verify-create")
    public Response<GoalCreateValidateResponse> validateCreateGoal(
            final UUID memberId
    ) {
        goalService.validateCreateGoal(memberId);
        return Response.ok("목표 생성 여부", GoalCreateValidateResponse.of(true));
    }

    @PostMapping
    public Response<GoalResponse> createGoal(
            final UUID memberId,
            @RequestBody @Valid final GoalCreateRequest request
    ) {
        GoalResponse response = goalService.createGoal(memberId, request);
        return Response.create("목표가 정상적으로 생성되었습니다.", response);
    }

    @PatchMapping("/{goalId}")
    public Response<GoalResponse> updateGoal(
            final UUID memberId,
            @PathVariable final Long goalId,
            @RequestBody @Valid final GoalUpdateRequest request
    ) {
        GoalResponse response = goalService.updateGoal(memberId, goalId, request);
        return Response.ok("목표가 정상적으로 수정되었습니다.", response);
    }

    @PatchMapping("/{goalId}/change-order")
    public void changeGoalSequence(
            final UUID memberId,
            @PathVariable final Long goalId,
            @RequestBody @Valid final GoalSequenceChangeRequest request
    ) {
        goalService.changeSequence(memberId, goalId, request);
    }

    @DeleteMapping("/{goalId}")
    public Response<GoalResponse> deleteGoal(
            final UUID memberId,
            @PathVariable final Long goalId
    ) {
        GoalResponse response = goalService.deleteGoal(memberId, goalId);
        return Response.ok("목표가 정상적으로 삭제되었습니다.", response);
    }
}
