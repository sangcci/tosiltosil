package tosiltosil.backend.module.terms.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.terms.domain.TermsVersion;

public interface TermsVersionJpaRepository extends JpaRepository<TermsVersion, Long> {
}
