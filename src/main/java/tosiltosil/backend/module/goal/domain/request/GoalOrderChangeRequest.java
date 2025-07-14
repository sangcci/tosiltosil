package tosiltosil.backend.module.goal.domain.request;

public record GoalOrderChangeRequest(
        Long goalId,
        Integer targetIndex,
        String prevOrderKey,
        String nextOrderKey
) {

}
