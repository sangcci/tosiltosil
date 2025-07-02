package tosiltosil.backend.module.member.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.member.domain.LocalAccount;

public interface LocalAccountJpaRepository extends JpaRepository<LocalAccount, Long> {
}
