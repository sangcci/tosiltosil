package tosiltosil.backend.module.goal.domain.response;

public record GoalCreateVerifyResponse(
        boolean canCreate
) {

    public static GoalCreateVerifyResponse of(boolean canCreate) {
        return new GoalCreateVerifyResponse(canCreate);
    }
}
