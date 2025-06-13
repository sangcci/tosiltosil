package tosiltosil.backend.module.category.domain;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Optional<Category> findById(Long categoryId);

    Optional<Category> findByIdAndMemberId(Long categoryId, UUID memberId);

    Long countByMemberId(UUID memberId);

    Category save(Category category);

    void delete(Category category);
}
