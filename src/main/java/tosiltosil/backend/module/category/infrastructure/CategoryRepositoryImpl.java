package tosiltosil.backend.module.category.infrastructure;

import java.time.LocalDate;
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
    public List<Category> findCategoriesByMemberIdAndDate(final UUID memberId, final LocalDate date) {
        return categoryJpaRepository.findByMemberIdAndDate(memberId, date);
    }

    @Override
    public List<CategoryColorPerDayResponse> findColorsPerMonth(final UUID memberId, final YearMonth yearMonth) {
        return categoryDslRepository.findColorsPerMonth(memberId, yearMonth);
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
