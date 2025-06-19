package tosiltosil.backend.module.terms.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.terms.domain.Terms;

import java.util.Optional;

public interface TermsJpaRepository extends JpaRepository<Terms, Long> {
    Optional<Terms> findByTitleAndVersion(String title, String version);
}
