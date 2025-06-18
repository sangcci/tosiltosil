package tosiltosil.backend.module.terms.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.terms.domain.MemberTerms;

public interface MemberTermsJpaRepository extends JpaRepository<MemberTerms, Long> {
}
