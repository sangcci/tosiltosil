package tosiltosil.backend.module.goal.domain.response;

public record GoalOrderChangeResponse(
        String orderKey
) {

    public static GoalOrderChangeResponse of(final String orderKey) {
        return new GoalOrderChangeResponse(orderKey);
    }
}
