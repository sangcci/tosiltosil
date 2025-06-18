package tosiltosil.backend.module.terms.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.terms.domain.Terms;

public interface TermsJpaRepository extends JpaRepository<Terms, Long> {
}
