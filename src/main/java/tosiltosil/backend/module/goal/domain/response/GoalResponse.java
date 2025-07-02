package tosiltosil.backend.module.goal.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoalResponse(
        Long goalId,
        List<Long> goalIds
) {

    public static GoalResponse ofSingle(final Long goalId) {
        return new GoalResponse(goalId, null);
    }

    public static GoalResponse ofList(final List<Long> goalIds) {
        return new GoalResponse(null, goalIds);
    }
}
