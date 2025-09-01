package tosiltosil.backend.module.progress.application;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.module.progress.domain.Progress;
import tosiltosil.backend.module.progress.domain.ProgressRepository;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;

    @Transactional(readOnly = true)
    public Duration getTodayDuration(final UUID memberId) {
        return progressRepository.findTodayDurationByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public Duration updateTodayDuration(final UUID memberId, final Duration duration) {
        // TODO: empty일 경우 처리
        Progress progress = progressRepository.findByMemberId(memberId).get();

        progress.addTodayDuration(duration);
        progressRepository.save(progress);

        return progress.getTodayDuration();
    }

    @Transactional(readOnly = true)
    public Duration subtractTodayDuration(final UUID memberId, final Duration duration) {
        // TODO: empty일 경우 처리
        Progress progress = progressRepository.findByMemberId(memberId).get();

        progress.subtractTodayDuration(duration);
        progressRepository.save(progress);

        return progress.getTodayDuration();
    }
}
