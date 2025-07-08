package tosiltosil.backend.module.category.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Optional<Category> findById(Long categoryId);

    List<Category> findCategoriesByMemberIdAndDate(UUID memberId, LocalDate date);

    Long countByMemberId(UUID memberId);

    Category save(Category category);

    void delete(Category category);
}
