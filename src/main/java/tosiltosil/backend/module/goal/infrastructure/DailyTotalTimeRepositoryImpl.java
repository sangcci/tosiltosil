package tosiltosil.backend.module.goal.infrastructure;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.goal.domain.DailyTotalTime;
import tosiltosil.backend.module.goal.domain.DailyTotalTimeRepository;

@Repository
@RequiredArgsConstructor
public class DailyTotalTimeRepositoryImpl implements DailyTotalTimeRepository {

    private final DailyTotalTimeJpaRepository dailyTotalTimeJpaRepository;

    @Override
    public DailyTotalTime findByMemberId(final UUID memberId) {
        return dailyTotalTimeJpaRepository.findByMemberId(memberId);
    }

    @Override
    public void save(final DailyTotalTime dailyTotalTime) {
        dailyTotalTimeJpaRepository.save(dailyTotalTime);
    }
}
