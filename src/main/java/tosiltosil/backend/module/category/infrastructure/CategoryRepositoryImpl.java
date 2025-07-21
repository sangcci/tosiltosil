package tosiltosil.backend.module.category.infrastructure;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.category.domain.Category;
import tosiltosil.backend.module.category.domain.CategoryRepository;
import tosiltosil.backend.module.category.domain.response.CategoryColorPerDayResponse;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryDslRepository categoryDslRepository;

    @Override
    public Optional<Category> findById(final Long categoryId) {
        return categoryJpaRepository.findById(categoryId);
    }

    @Override
    public List<Category> findCurrentCategories(final UUID memberId) {
        return categoryJpaRepository.findByMemberIdOrderByOrderIndex(memberId);
    }

    @Override
    public List<CategoryColorPerDayResponse> findColorsPerMonth(final UUID memberId, final YearMonth yearMonth) {
        return categoryDslRepository.findColorsPerMonth(memberId, yearMonth);
    }

    @Override
    public Long countCurrentCategory(final UUID memberId) {
        return categoryJpaRepository.countByMemberId(memberId);
    }

    @Override
    public Optional<BigDecimal> findLastOrderIndex(final UUID memberId) {
        return categoryJpaRepository.findMaxOrderIndexByMemberId(memberId);
    }

    @Override
    public Category save(final Category category) {
        return categoryJpaRepository.save(category);
    }

    @Override
    public List<Category> saveAll(final List<Category> categories) {
        // TODO: bulk insert 구현
        return categoryJpaRepository.saveAll(categories);
    }

    @Override
    public void delete(final Category category) {
        categoryJpaRepository.delete(category);
    }
}
