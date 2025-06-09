package tosiltosil.backend.module.goal.domain.request;

import java.time.LocalDate;

public record GoalUpdateRequest(
        String title,
        Long iconId,
        Long categoryId,
        LocalDate date,
        Long time
) {

}
