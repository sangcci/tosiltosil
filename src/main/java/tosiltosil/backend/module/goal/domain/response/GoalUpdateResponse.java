package tosiltosil.backend.module.goal.domain.response;

public record GoalUpdateResponse(
        Long goalId
) {

    public static GoalUpdateResponse of(Long goalId) {
        return new GoalUpdateResponse(goalId);
    }
}
