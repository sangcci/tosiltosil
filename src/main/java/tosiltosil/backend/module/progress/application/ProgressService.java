package tosiltosil.backend.module.progress.application;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.common.logging.domain.InfoLog;
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
        Optional<Progress> progressOptional = progressRepository.findByMemberId(memberId);
        Progress progress = progressOptional.orElseGet(() -> {
            InfoLog.of("계정 생성 시 Progress가 생성되지 않아 지금 생성합니다. 계정 생성 부분을 확인해주세요");
            return Progress.of(memberId, Duration.ZERO, 0);
        });

        progress.addTodayDuration(duration);
        progressRepository.save(progress);

        return progress.getTodayDuration();
    }

    @Transactional(readOnly = true)
    public Duration subtractTodayDuration(final UUID memberId, final Duration duration) {
        Optional<Progress> progressOptional = progressRepository.findByMemberId(memberId);
        Progress progress = progressOptional.orElseGet(() -> {
            InfoLog.of("계정 생성 시 Progress가 생성되지 않아 지금 생성합니다. 계정 생성 부분을 확인해주세요");
            return Progress.of(memberId, Duration.ZERO, 0);
        });

        progress.subtractTodayDuration(duration);
        progressRepository.save(progress);

        return progress.getTodayDuration();
    }
}
