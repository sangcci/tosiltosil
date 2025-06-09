package tosiltosil.backend.module.goal.domain.request;

public record GoalSequenceChangeRequest(
        Long goalId,
        Long sequence
) {

}
