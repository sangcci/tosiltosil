package tosiltosil.backend.module.progress.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.progress.domain.Progress;
import tosiltosil.backend.module.progress.domain.ProgressRepository;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProgressRepositoryImpl implements ProgressRepository {

    private final ProgressJpaRepository progressJpaRepository;
    private final ProgressRedisRepository progressRedisRepository;

    @Override
    public Optional<Progress> findByMemberId(final UUID memberId) {
        Optional<Progress> progressOptional = progressJpaRepository.findByMemberId(memberId);
        if (progressOptional.isEmpty()) return progressOptional;
        Progress progress = progressOptional.get();
        progress.setTodayDuration(progressRedisRepository.findTodayDuration(memberId));
        return Optional.of(progress);
    }

    @Override
    public void save(final Progress progress) {
        progressJpaRepository.save(progress);
        progressRedisRepository.cacheTodayDuration(progress.getMemberId(), progress.getTodayDuration());
    }

    @Override
    public Duration findTodayDurationByMemberId(final UUID memberId) {
        return progressRedisRepository.findTodayDuration(memberId);
    }

    @Override
    public void saveTodayDuration(final UUID memberId, final Duration duration) {
        progressRedisRepository.cacheTodayDuration(memberId, duration);
    }
}
