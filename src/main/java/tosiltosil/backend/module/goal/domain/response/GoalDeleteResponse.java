package tosiltosil.backend.module.goal.domain.response;

public record GoalDeleteResponse(
        Long goalId
) {

    public static GoalDeleteResponse of(Long goalId) {
        return new GoalDeleteResponse(goalId);
    }
}
