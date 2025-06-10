package tosiltosil.backend.module.goal.domain.response;

public record GoalCreateValifyResponse(
        boolean canCreate
) {

    public static GoalCreateValifyResponse of(boolean canCreate) {
        return new GoalCreateValifyResponse(canCreate);
    }
}
