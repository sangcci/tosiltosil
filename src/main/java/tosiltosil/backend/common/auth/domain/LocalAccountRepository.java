package tosiltosil.backend.common.auth.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalAccountRepository extends JpaRepository<LocalAccount, Long> {

    Optional<LocalAccount> findByEmail(String email);
}
