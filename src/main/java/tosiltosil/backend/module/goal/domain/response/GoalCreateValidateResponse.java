package tosiltosil.backend.module.goal.domain.response;

public record GoalCreateValidateResponse(
        boolean canCreate
) {

    public static GoalCreateValidateResponse of(boolean canCreate) {
        return new GoalCreateValidateResponse(canCreate);
    }
}
