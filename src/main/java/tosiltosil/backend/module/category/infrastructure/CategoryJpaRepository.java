package tosiltosil.backend.module.category.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.category.domain.Category;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    List<Category> findByMemberId(UUID memberId);

    Long countByMemberId(UUID memberId);

    List<Category> findByMemberIdOrderByOrderKey(UUID memberId);
}
