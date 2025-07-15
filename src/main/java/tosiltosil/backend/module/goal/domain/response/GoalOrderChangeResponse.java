package tosiltosil.backend.module.goal.domain.response;

public record GoalOrderChangeResponse(
        Double orderIndex
) {

    public static GoalOrderChangeResponse of(final Double orderIndex) {
        return new GoalOrderChangeResponse(orderIndex);
    }
}
