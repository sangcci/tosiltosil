package tosiltosil.backend.module.category.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tosiltosil.backend.module.category.domain.Category;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    List<Category> findByMemberIdOrderByOrderIndex(UUID memberId);

    Long countByMemberId(UUID memberId);

    @Query("SELECT MAX(c.orderIndex) FROM Category c WHERE c.memberId = :memberId")
    Optional<Double> findMaxOrderIndexByMemberId(@Param("memberId") UUID memberId);
}
