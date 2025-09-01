package tosiltosil.backend.module.progress.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.progress.domain.Progress;

import java.util.Optional;
import java.util.UUID;

public interface ProgressJpaRepository extends JpaRepository<Progress, Long> {

    Optional<Progress> findByMemberId(UUID memberId);
}
