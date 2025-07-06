package tosiltosil.backend.module.goal.domain.response;

import java.util.List;

public record GoalIdsResponse(
        List<Long> goalIds
) {

    public static GoalIdsResponse of(final List<Long> goalIds) {
        return new GoalIdsResponse(goalIds);
    }
}
