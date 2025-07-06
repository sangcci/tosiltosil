package tosiltosil.backend.module.goal.domain.response;

public record GoalIdResponse(
        Long goalId
) {

    public static GoalIdResponse of(final Long goalId) {
        return new GoalIdResponse(goalId);
    }
}
