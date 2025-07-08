package tosiltosil.backend.module.goal.domain.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.holder.TimeHolder;

@Component
@RequiredArgsConstructor
public class GoalDomainService {

    private final TimeHolder timeHolder;

    public void validateGoalDate(final LocalDate date) {
        LocalDate today = timeHolder.getCurrentDate();
        if (date.isBefore(today)) {
            throw new BadRequestException("날짜는 오늘 이후여야 합니다.");
        }
    }
}