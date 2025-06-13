package tosiltosil.backend.module.category.infrastructure;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.category.domain.Category;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndMemberId(Long id, UUID memberId);

    Long countByMemberId(UUID memberId);
}
