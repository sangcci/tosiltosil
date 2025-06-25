package tosiltosil.backend.module.duration.infrastructure;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.duration.domain.DailyDuration;
import tosiltosil.backend.module.duration.domain.DurationRepository;

@Repository
@RequiredArgsConstructor
public class DurationRepositoryImpl implements DurationRepository {

    private final DurationJpaRepository durationJpaRepository;
    private final DurationRedisRepository durationRedisRepository;

    @Override
    public DailyDuration findByMemberId(final UUID memberId) {
        return durationJpaRepository.findByMemberId(memberId);
    }

    @Override
    public Duration findTodayDuration(final UUID memberId) {
        return durationRedisRepository.findTodayDuration(memberId);
    }

    @Override
    public void save(final DailyDuration dailyDuration) {
        durationJpaRepository.save(dailyDuration);
    }

    @Override
    public void saveTodayDuration(final UUID memberId, final Duration duration) {
        durationRedisRepository.cacheTodayDuration(memberId, duration);
    }
}
