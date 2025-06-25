package tosiltosil.backend.module.goal.domain.request;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import tosiltosil.backend.module.goal.domain.Goal;

public record GoalCreateRequest(
        String title,
        Long iconId,
        Long categoryId,
        List<LocalDate> dates,
        Duration time
) {

    public List<Goal> toEntities(
            final UUID memberId
            //final int sequence
    ) {
        return dates.stream()
                .map(date -> Goal.of(memberId, categoryId, title, time, 1, iconId, date))
                .toList();
    }
}
