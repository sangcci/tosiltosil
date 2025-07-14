package tosiltosil.backend.module.duration.infrastructure;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.duration.domain.DurationRepository;

@Repository
@RequiredArgsConstructor
public class DurationRepositoryImpl implements DurationRepository {

    private final DurationRedisRepository durationRedisRepository;

    @Override
    public Duration findTodayDuration(final UUID memberId) {
        return durationRedisRepository.findTodayDuration(memberId);
    }

    @Override
    public void saveTodayDuration(final UUID memberId, final Duration duration) {
        durationRedisRepository.cacheTodayDuration(memberId, duration);
    }
}
