package tosiltosil.backend.module.category.infrastructure;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.category.domain.Category;
import tosiltosil.backend.module.category.domain.CategoryRepository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Optional<Category> findById(final Long categoryId) {
        return categoryJpaRepository.findById(categoryId);
    }

    @Override
    public Optional<Category> findByIdAndMemberId(final Long categoryId, final UUID memberId) {
        return categoryJpaRepository.findByIdAndMemberId(categoryId, memberId);
    }

    @Override
    public Long countByMemberId(final UUID memberId) {
        return categoryJpaRepository.countByMemberId(memberId);
    }

    @Override
    public Category save(final Category category) {
        return categoryJpaRepository.save(category);
    }

    @Override
    public void delete(final Category category) {
        categoryJpaRepository.delete(category);
    }
}
