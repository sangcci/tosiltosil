package tosiltosil.backend.module.goal.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import tosiltosil.backend.module.goal.domain.response.GoalDeleteResponse;
import tosiltosil.backend.module.goal.domain.response.GoalUpdateResponse;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public void createGoal(
            final UUID memberId,
            @RequestBody @Valid final GoalCreateRequest request
    ) {
        goalService.createGoal(memberId, request);
    }

    @PatchMapping("/{goalId}")
    public Response<GoalUpdateResponse> updateGoal(
            final UUID memberId,
            @PathVariable @NotNull(message = "목표 ID가 유효하지 않습니다. 목표 ID 숫자를 입력해야 합니다.") final Long goalId,
            @RequestBody @Valid final GoalUpdateRequest request
    ) {
        GoalUpdateResponse response = goalService.updateGoal(memberId, goalId, request);
        return Response.ok("목표가 정상적으로 수정되었습니다.", response);
    }

    @PatchMapping("/{goalId}/change-order")
    public void changeGoalSequence(
            final UUID memberId,
            @PathVariable @NotNull(message = "목표 ID가 유효하지 않습니다. 목표 ID 숫자를 입력해야 합니다.") final Long goalId,
            @RequestBody @Valid final GoalSequenceChangeRequest request
    ) {
        goalService.changeSequence(memberId, goalId, request);
    }

    @DeleteMapping("/{goalId}")
    public Response<GoalDeleteResponse> deleteGoal(
            final UUID memberId,
            @PathVariable @NotNull(message = "목표 ID가 유효하지 않습니다. 목표 ID 숫자를 입력해야 합니다.") final Long goalId
    ) {
        GoalDeleteResponse response = goalService.deleteGoal(memberId, goalId);
        return Response.ok("목표가 정상적으로 삭제되었습니다.", response);
    }
}
