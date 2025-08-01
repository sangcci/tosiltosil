package tosiltosil.backend.module.goal.domain.request;

import jakarta.validation.constraints.Min;

public record GoalOrderChangeRequest(
        @Min(value = 1, message = "순서는 1 이상이어야 합니다.")
        int targetPosition
) {

}
